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

import com.example.myapplication.Controller.AuditController;
import com.example.myapplication.Controller.LedResourceController;
import com.example.myapplication.data.ViewSharer;
import com.example.myapplication.ui.ManageAuditAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ManageAuditActivity extends AppCompatActivity {
    private SearchView searchView;
    private RecyclerView recyclerView;
    private Retrofit retrofit;
    private AuditController auditController;
    private LedResourceController ledResourceController;
    private ManageAuditAdapter manageAuditAdapter;
    private ViewSharer viewSharer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manager_caution_resouce);
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
        auditController=retrofit.create(AuditController.class);
        ledResourceController=retrofit.create(LedResourceController.class);
        searchView=findViewById(R.id.manager_search_box);
        recyclerView=findViewById(R.id.recycler_audit);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        manageAuditAdapter=new ManageAuditAdapter(new ArrayList<>(),auditController,this,ledResourceController,viewSharer.getUser().getUserId());
        recyclerView.setAdapter(manageAuditAdapter);
        initAudit();
    }
    private void initAudit(){
        Call<List<Audit>> call=auditController.getAllAudits();
        call.enqueue(new Callback<List<Audit>>() {
            @Override
            public void onResponse(Call<List<Audit>> call, Response<List<Audit>> response) {
                if (response.body()!=null){
                    manageAuditAdapter.updateList(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Audit>> call, Throwable t) {
                Toast.makeText(ManageAuditActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
