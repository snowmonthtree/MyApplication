package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.page.Login.LoginActivity;

import java.io.IOException;

public class TextActivity extends AppCompatActivity {

    private EditText inputText;
    private Button generateButton;
    private Button saveButton;
    private ImageView generatedImageView;
    private Bitmap generatedBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        inputText = findViewById(R.id.inputText);
        generateButton = findViewById(R.id.generateButton);
        saveButton = findViewById(R.id.saveButton);
        generatedImageView = findViewById(R.id.generatedImageView);

        // 生成按钮的点击事件
        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = inputText.getText().toString();
                generatedBitmap = generateImage(text);
                generatedImageView.setImageBitmap(generatedBitmap);
            }
        });

        // 保存按钮的点击事件
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (generatedBitmap != null) {

                    save();
                } else {
                    Toast.makeText(TextActivity.this, "请先生成图像", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 生成图像
    private Bitmap generateImage(String text) {
        int width = 32;  // 图像宽度
        int height = 8;  // 图像高度

        // 创建一个空白的位图
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        // 创建画布
        Canvas canvas = new Canvas(bitmap);

        // 创建画笔
        Paint paint = new Paint();
        paint.setColor(Color.RED);  // 设置文字颜色为红色
        paint.setTextSize(8);  // 设置字体大小为8
        paint.setTextAlign(Paint.Align.LEFT);  // 文字左对齐

        // 设置背景为黑色
        canvas.drawColor(Color.BLACK);

        // 在画布上绘制文字
        canvas.drawText(text, 0, height - 2, paint);  // 设置文字位置

        return bitmap;
    }


    public void save(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // 如果没有权限，请求权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    100);
        }
        //将图片保存为LED资源
        LEDResource resource = new LEDResource(32, 8);
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 32; x++) {
                int pixelColor = generatedBitmap.getPixel(x, y);
                resource.setColor(x, y, pixelColor);
            }
        }

        try {
            resource.saveBitmapToFile(resource.toBitmap(),System.currentTimeMillis()+"",getFilesDir());
            /*MediaScannerConnection.scanFile(this,
                    new String[]{Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()},
                    null,
                    (path, uri) -> Log.d("MediaScanner", "File scanned: " + path));*/

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Toast.makeText(this, "LED Resource Saved:\n" + resource.toString(), Toast.LENGTH_SHORT).show();
        new AlertDialog.Builder(this)
                .setTitle("成功")
                .setMessage("新生成的图像已保存,如果要上传,请到创作中心->本地资源->选择相应的图片上传")
                .setPositiveButton("确定", (dialog, which) -> {
                    // 确定按钮的点击事件
                })
                .show();
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
