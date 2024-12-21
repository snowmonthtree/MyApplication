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

import com.example.myapplication.Controller.CommentsController;
import com.example.myapplication.Controller.LedResourceController;
import com.example.myapplication.data.Comment.Comment;
import com.example.myapplication.data.ViewSharer;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ManageCommentActivity extends AppCompatActivity {
    private SearchView searchView;
    private RecyclerView recyclerView;
    private Retrofit retrofit;
    private CommentsController commentsController;
    private ManageCommentAdapter manageCommentAdapter;
    private ViewSharer viewSharer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manager_comments);
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
        commentsController=retrofit.create(CommentsController.class);
        searchView=findViewById(R.id.manager_search_box);
        recyclerView=findViewById(R.id.recycler_comment);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        manageCommentAdapter=new ManageCommentAdapter(new ArrayList<>(),commentsController,this);// 先传入一个空列表
        recyclerView.setAdapter(manageCommentAdapter);
        initComment();
    }
    private void initComment(){
        Call<List<Comment>> call=commentsController.getAllComments();
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if (response.body()!=null) {
                    manageCommentAdapter.updateData(response.body());
                }
                else {

                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Toast.makeText(ManageCommentActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}