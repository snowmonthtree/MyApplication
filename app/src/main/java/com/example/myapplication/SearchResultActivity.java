package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import android.view.inputmethod.InputMethodManager;

import com.example.myapplication.data.Result.ResultItem;

public class SearchResultActivity extends AppCompatActivity {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private TextView noResultsTextView;
    private ResultAdapter resultsAdapter;
    private List<ResultItem> resultList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        // 初始化视图
        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);
        noResultsTextView = findViewById(R.id.noResultsTextView);

        // 获取传递过来的查询词
        Intent intent = getIntent();
        String query = intent.getStringExtra("query");

        // 初始化结果列表
        resultList = new ArrayList<>();
        resultList.addAll(getSampleData(query));

        // 设置 RecyclerView
        resultsAdapter = new ResultAdapter(resultList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(resultsAdapter);

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
        sampleData.add(new ResultItem("结果1", "这是一个描述", R.drawable.test));
        sampleData.add(new ResultItem("结果2", "这是另一个描述", R.drawable.ic_launcher_foreground));
        sampleData.add(new ResultItem("结果3", "这是第三个描述", R.drawable.ic_launcher_foreground));

        List<ResultItem> filteredList = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            Log.d("SearchResultActivity", "Query is empty or null, returning all data");
            return sampleData;
        }

        for (ResultItem item : sampleData) {
            String title = item.getTitle();
            Log.d("SearchResultActivity", "Title: " + title);
            if (title != null && title.toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }
        return filteredList;
    }

    private void hideKeyboard() {
        // 隐藏键盘的方法
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}