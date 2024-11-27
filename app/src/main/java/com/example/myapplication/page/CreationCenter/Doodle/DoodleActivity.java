package com.example.myapplication.page.CreationCenter.Doodle;

import android.os.Bundle;
import android.widget.Button;
import android.graphics.Bitmap;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.DoodleView;
import com.example.myapplication.R;

public class DoodleActivity extends AppCompatActivity {

    private DoodleView doodleView;
    private Button buttonClear, buttonUndo, buttonRedo, buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doodle);

        doodleView = findViewById(R.id.doodle_view);
        buttonClear = findViewById(R.id.button1);
        buttonUndo = findViewById(R.id.button2);
        buttonRedo = findViewById(R.id.button3);
        buttonSave = findViewById(R.id.button4);

        // 使用 Lambda 表达式设置按钮点击事件
        buttonClear.setOnClickListener(v -> doodleView.clear());
        buttonUndo.setOnClickListener(v -> doodleView.undo());
        buttonRedo.setOnClickListener(v -> doodleView.redo());
        buttonSave.setOnClickListener(v -> {
            Bitmap bitmap = doodleView.getBitmap();
            // 保存 bitmap 到文件或分享
        });
    }
}