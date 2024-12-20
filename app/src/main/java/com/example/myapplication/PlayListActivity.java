package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Controller.LedListController;
import com.example.myapplication.data.PlayList.PlayList;
import com.example.myapplication.data.ViewSharer;
import com.example.myapplication.page.Login.LoginActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PlayListActivity extends AppCompatActivity {
    private Retrofit retrofit;
    private LedListController ledListController;
    private RecyclerView recyclerView;
    private PlayListAdapter playListAdapter;
    private Button button;
    private EditText editText;
    private ViewSharer viewSharer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_play_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        try {
            retrofit = RetrofitClient.getClient(PlayListActivity.this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ledListController=retrofit.create(LedListController.class);
        recyclerView=findViewById(R.id.recycler_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        playListAdapter = new PlayListAdapter(new ArrayList<PlayList>(),this); // 先传入一个空列表
        recyclerView.setAdapter(playListAdapter);
        button=findViewById(R.id.createNewList);
        editText=findViewById(R.id.newListName);
        button.setOnClickListener(view -> CreateNewList());
        viewSharer=(ViewSharer)getApplication();
        InitList();
    }
    private void InitList(){
        Call<List<PlayList>> call=ledListController.getPlaylists(viewSharer.getUser().getUserId());
        call.enqueue(new Callback<List<PlayList>>() {
            @Override
            public void onResponse(Call<List<PlayList>> call, Response<List<PlayList>> response) {
                playListAdapter.updateData(response.body());
            }

            @Override
            public void onFailure(Call<List<PlayList>> call, Throwable t) {
                Toast.makeText(PlayListActivity.this, "error"+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void CreateNewList(){
        Call<String> call=ledListController.createPlaylist(viewSharer.getUser().getUserId(),editText.getText().toString());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Toast.makeText(PlayListActivity.this, response.body(), Toast.LENGTH_SHORT).show();
                InitList();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(PlayListActivity.this, "error"+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}