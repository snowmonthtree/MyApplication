package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Controller.LedResourceController;
import com.example.myapplication.data.LedResource.LedResource;
import com.example.myapplication.data.ViewSharer;
import com.example.myapplication.ui.ManageResourceAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ManageResourceActivity  extends AppCompatActivity {
    private SearchView searchView;
    private RecyclerView recyclerView;
    private Retrofit retrofit;
    private LedResourceController ledResourceController;
    private ManageResourceAdapter manageResourceAdapter;
    private ViewSharer viewSharer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manager_resources);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        try {
            retrofit = RetrofitClient.getClient(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        viewSharer =(ViewSharer)getApplication();
        ledResourceController=retrofit.create(LedResourceController.class);
        searchView=findViewById(R.id.manager_search_box);
        recyclerView=findViewById(R.id.recycler_resource);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        manageResourceAdapter=new ManageResourceAdapter(new ArrayList<>(),ledResourceController,this);// 先传入一个空列表
        recyclerView.setAdapter(manageResourceAdapter);
        if(viewSharer.getUser().getPermissionId().equals("1")){
            initResource();
        }
        else if (viewSharer.getUser().getPermissionId().equals("0")){
            initResourceByUser();
            searchView.setVisibility(View.GONE);
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 当用户提交搜索时，更新结果列表
                searchResource(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // 当搜索文本变化时，更新结果列表
                if (newText != null && !newText.trim().isEmpty()) {
                    searchResource(newText);
                }
                return true;
            }
        });
    }
    private void initResource(){
        Call<List<LedResource>> call=ledResourceController.getLatestLedResources();
        call.enqueue(new Callback<List<LedResource>>() {
            @Override
            public void onResponse(Call<List<LedResource>> call, Response<List<LedResource>> response) {
                if (response.body()!=null) {
                    manageResourceAdapter.updateData(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<LedResource>> call, Throwable t) {
                Toast.makeText(ManageResourceActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void initResourceByUser(){
        Call<List<LedResource>> call=ledResourceController.getLatestLedResources();
        call.enqueue(new Callback<List<LedResource>>() {
            @Override
            public void onResponse(Call<List<LedResource>> call, Response<List<LedResource>> response) {
                if (response.body()!=null) {
                    List<LedResource> list=new ArrayList<>();
                    for (LedResource ledResource:response.body()){
                        if (ledResource.getUser().getUserId().equals(viewSharer.getUser().getUserId())){
                            list.add(ledResource);
                        }
                    }
                    manageResourceAdapter.updateData(list);
                }
            }

            @Override
            public void onFailure(Call<List<LedResource>> call, Throwable t) {
                Toast.makeText(ManageResourceActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void searchResource(String query){
        Call<List<LedResource>> call=ledResourceController.searchLedResources(query,query);
        call.enqueue(new Callback<List<LedResource>>() {
            @Override
            public void onResponse(Call<List<LedResource>> call, Response<List<LedResource>> response) {
                if (response.body()!=null) {
                    manageResourceAdapter.updateData(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<LedResource>> call, Throwable t) {
                Toast.makeText(ManageResourceActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
