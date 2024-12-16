package com.example.myapplication.page.Search;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.myapplication.Controller.LedResourceController;
import com.example.myapplication.R;
import com.example.myapplication.RetrofitClient;
import com.example.myapplication.data.LedResource.LedResource;
import com.example.myapplication.data.Result.ResultItem;
import com.example.myapplication.ui.ResultAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchResultActivity extends AppCompatActivity {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private TextView noResultsTextView;
    private ResultAdapter resultsAdapter;
    private List<ResultItem> resultList;
    private Retrofit retrofit;
    private LedResourceController ledResourceController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        // 初始化视图
        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);
        noResultsTextView = findViewById(R.id.noResultsTextView);
        try {
            retrofit = RetrofitClient.getClient(SearchResultActivity.this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ledResourceController = retrofit.create(LedResourceController.class);

        // 获取传递过来的查询词
        Intent intent = getIntent();
        String query = intent.getStringExtra("search_query");

        // 初始化结果列表
        resultList = new ArrayList<>();
        resultList.addAll(getSampleData(query));
        // 设置 RecyclerView
        resultsAdapter = new ResultAdapter(resultList,ledResourceController,this);
        recyclerView.setAdapter(resultsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchResultActivity.this));


        // 设置搜索栏的监听器
        searchView.setQuery(query, false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 当用户提交搜索时，更新结果列表
                updateResults(query);
                hideKeyboard(); // 隐藏键盘
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // 当搜索文本变化时，更新结果列表
                if (newText != null && !newText.trim().isEmpty()) {
                    updateResults(newText);
                }
                return true;
            }
        });
    }

    private void updateResults(String query) {
        List<ResultItem> filteredList = getSampleData(query);
        resultsAdapter.updateData(filteredList);

        if (filteredList.isEmpty()) {
            noResultsTextView.setVisibility(View.VISIBLE);
        } else {
            noResultsTextView.setVisibility(View.GONE);
        }
    }

    private List<ResultItem> getSampleData(String query) {
        // 模拟数据，实际应用中应从服务器或其他数据源获取
        List<ResultItem> sampleData = new ArrayList<>();
        Call<List<LedResource>> call=ledResourceController.searchLedResources(query,query);
        call.enqueue(new Callback<List<LedResource>>() {
            @Override
            public void onResponse(Call<List<LedResource>> call, Response<List<LedResource>> response) {
                if (response.isSuccessful()&&response.body()!=null) {

                    List<LedResource> list = response.body();
                    System.out.println(list);
                    for (LedResource ledResource : list) {
                        sampleData.add(new ResultItem(ledResource.getName(), ledResource.getDetail(), ledResource.getViewWebUrl(),ledResource.getResourceId()));
                    System.out.println(sampleData);
                    }
                }
                else {
                    sampleData.add(new ResultItem("错误","网络问题","image10.jpg","1"));
                }
                resultsAdapter.updateData(sampleData);


            }

            @Override
            public void onFailure(Call<List<LedResource>> call, Throwable t) {
                Toast.makeText(SearchResultActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
        return sampleData;


       /* for (ResultItem item : sampleData) {
            String title = item.getTitle();
            Log.d("SearchResultActivity", "Title: " + title);
            if (title != null && title.toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }
        return filteredList;*/
    }

    private void hideKeyboard() {
        // 隐藏键盘的方法
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}