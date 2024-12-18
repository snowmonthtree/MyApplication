package com.example.myapplication.page.CreationCenter.Doodle;

import static android.os.Environment.getExternalStoragePublicDirectory;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.DrawingView;
import com.example.myapplication.LEDResource;

import com.example.myapplication.R;

import java.io.IOException;

public class DoodleActivity extends AppCompatActivity {

    private DrawingView drawingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doodle);

        // 初始化画布
        drawingView = findViewById(R.id.doodle_canvas);

        // 颜色选择器
        SeekBar colorSeekBar = findViewById(R.id.color_seekbar);
        colorSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float[] hsv = {progress, 1.0f, 1.0f};  // 根据 SeekBar 的进度更新颜色
                int color = Color.HSVToColor(hsv);
                drawingView.setPaintColor(color);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // 保存按钮
        Button saveButton = findViewById(R.id.btn_save);
        saveButton.setOnClickListener(view -> save());

        // 清空按钮
        Button clearButton = findViewById(R.id.btn_clear);
        clearButton.setOnClickListener(v -> drawingView.clearCanvas());

        // 撤销按钮
        Button undoButton = findViewById(R.id.btn_undo);
        undoButton.setOnClickListener(v -> drawingView.undoLastAction());
    }
    public void save(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // 如果没有权限，请求权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    100);
        }
        LEDResource resource = drawingView.saveAsLEDResource();
        try {
            resource.saveBitmapToFile(resource.toBitmap(),"1",getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
            MediaScannerConnection.scanFile(this,
                    new String[]{getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()},
                    null,
                    (path, uri) -> Log.d("MediaScanner", "File scanned: " + path));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Toast.makeText(this, "LED Resource Saved:\n" + resource.toString(), Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // 判断请求码是否匹配
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限被授予，执行文件操作
                save();
            } else {
                // 权限被拒绝，提示用户
                Toast.makeText(this, "Permission denied, cannot access the file", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
