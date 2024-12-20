package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Controller.LedListController;
import com.example.myapplication.Controller.LedResourceController;
import com.example.myapplication.data.LedResource.LedResource;
import com.example.myapplication.data.PlayList.PlayList;
import com.example.myapplication.data.Result.ResultItem;
import com.example.myapplication.data.ViewSharer;
import com.example.myapplication.ui.ResultAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ListDetailActivity extends AppCompatActivity {
    private Retrofit retrofit;
    private LedListController ledListController;
    private LedResourceController ledResourceController;
    private RecyclerView recyclerView;
    private LedListAdapter ledListAdapter;
    private ViewSharer viewSharer;
    private String listId;
    private Button setList;
    private Button rename;
    private Button deleteList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_detail);
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
        Intent intent=getIntent();
        listId=intent.getStringExtra("listId");
        viewSharer=(ViewSharer)getApplication();
        ledListController=retrofit.create(LedListController.class);
        ledResourceController=retrofit.create(LedResourceController.class);
        deleteList=findViewById(R.id.deleteList);
        setList=findViewById(R.id.setAsNow);
        recyclerView=findViewById(R.id.recycler_list_detail);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ledListAdapter = new LedListAdapter(new ArrayList<ResultItem>(),ledResourceController,this); // 先传入一个空列表
        recyclerView.setAdapter(ledListAdapter);
        initList();
        setList.setOnClickListener(view -> setList());
        deleteList.setOnClickListener(view -> deleteList());
    }
    private void initList(){
        Call<List<LedResource>> call=ledListController.getPlaylistResources(viewSharer.getUser().getUserId(),listId);
        call.enqueue(new Callback<List<LedResource>>() {
            @Override
            public void onResponse(Call<List<LedResource>> call, Response<List<LedResource>> response) {
                List<ResultItem> list=new ArrayList<ResultItem>();
                for (LedResource ledResource : response.body()){
                    list.add(new ResultItem(ledResource.getName(),ledResource.getDetail(),ledResource.getViewWebUrl(),ledResource.getResourceId()));

                }
                ledListAdapter.updateData(list);
            }

            @Override
            public void onFailure(Call<List<LedResource>> call, Throwable t) {

            }
        });
    }
    private void setList(){
        viewSharer.setListId(listId);
    }
    private void deleteList(){
        Call<String> call=ledListController.deletePlaylist(viewSharer.getUser().getUserId(),listId);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Toast.makeText(ListDetailActivity.this, response.body(), Toast.LENGTH_SHORT).show();
                finish();
            }


            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(ListDetailActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}