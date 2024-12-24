package com.example.myapplication;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Controller.UserController;
import com.example.myapplication.data.User.User;
import com.example.myapplication.data.ViewSharer;
import com.example.myapplication.ui.ManageUserAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ManageUserActivity extends AppCompatActivity {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private Retrofit retrofit;
    private UserController userController;
    private ManageUserAdapter manageUserAdapter;
    private ViewSharer viewSharer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manager_user);
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
        userController=retrofit.create(UserController.class);
        searchView=findViewById(R.id.manager_search_box);
        recyclerView=findViewById(R.id.recyler_user);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        manageUserAdapter=new ManageUserAdapter(new ArrayList<User>(),userController,this, viewSharer.getUser().getUserId());// 先传入一个空列表
        recyclerView.setAdapter(manageUserAdapter);
        initUser();
    }
    private void initUser(){
        Call<List<User>> call=userController.getAllUser();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.body() != null) {
                    manageUserAdapter.updateData(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(ManageUserActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}