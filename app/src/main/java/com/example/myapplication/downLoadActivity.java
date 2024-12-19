package com.example.myapplication;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class downLoadActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LocalAdapter localAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_down_load);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recycler_download);
        List<Uri> localList = new ArrayList<>();
        File fileDir=new File(getFilesDir(),"download");
        File[] files = fileDir.listFiles();
        if (files != null) {
            // 遍历所有文件
            for (File file : files) {
                if (file.isFile()) {
                    // 处理文件
                    if (isImageFile(file)) {
                        localList.add(Uri.fromFile(file));
                    }
                } else if (file.isDirectory()) {
                    // 处理子目录
                    System.out.println("Directory: " + file.getName());
                }
            }
        } else {
            System.out.println("No files found in the directory.");
        }
        localAdapter = new LocalAdapter(localList, this);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setAdapter(localAdapter);
    }
    private boolean isImageFile(File file) {
        String[] imageExtensions = {".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp"};

        for (String extension : imageExtensions) {
            if (file.getName().toLowerCase().endsWith(extension)) {
                return true;
            }
        }

        return false;
    }
}