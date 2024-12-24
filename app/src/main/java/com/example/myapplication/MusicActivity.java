package com.example.myapplication;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import java.io.IOException;
import java.util.Map;

public class MusicActivity extends AppCompatActivity {

    private Bitmap generatedBitmap;

    private EditText inputText;
    private Button musicSymbol1, musicSymbol2, musicSymbol3, musicSymbol4,deleteButton,
            musicSymbol5, musicSymbol6, musicSymbol7, musicSymbol8, generateButton, saveButton;
    private ImageView generatedImageView;
    private StringBuilder inputStringBuilder;

    // 定义符号与图案的映射
    private Map<String, Bitmap> symbolToBitmapMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        inputText = findViewById(R.id.inputText);
        musicSymbol1 = findViewById(R.id.musicSymbol1);
        musicSymbol2 = findViewById(R.id.musicSymbol2);
        musicSymbol3 = findViewById(R.id.musicSymbol3);
        musicSymbol4 = findViewById(R.id.musicSymbol4);
        musicSymbol5 = findViewById(R.id.musicSymbol5);
        musicSymbol6 = findViewById(R.id.musicSymbol6);
        musicSymbol7 = findViewById(R.id.musicSymbol7);
        musicSymbol8 = findViewById(R.id.musicSymbol8);
        generateButton = findViewById(R.id.generateButton);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);
        generatedImageView = findViewById(R.id.generatedImageView);

        inputStringBuilder = new StringBuilder();



        // 设置符号按钮点击事件
        musicSymbol1.setOnClickListener(v -> appendSymbol("♪"));
        musicSymbol2.setOnClickListener(v -> appendSymbol("♫"));
        musicSymbol3.setOnClickListener(v -> appendSymbol("♬"));
        musicSymbol4.setOnClickListener(v -> appendSymbol("♩"));
        musicSymbol5.setOnClickListener(v -> appendSymbol("𝄞"));
        musicSymbol6.setOnClickListener(v -> appendSymbol("𝅘𝅥𝅮"));
        musicSymbol7.setOnClickListener(v -> appendSymbol("𝅘"));
        musicSymbol8.setOnClickListener(v -> appendSymbol("𝅘𝅥𝅱"));

        // 生成图片按钮点击事件
        generateButton.setOnClickListener(v -> generateImage());

        // 设置删除符号按钮点击事件
        deleteButton.setOnClickListener(v -> deleteLastSymbol());

        // 保存按钮的点击事件
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (generatedBitmap != null) {
                    save();
                } else {
                    Toast.makeText(MusicActivity.this, "请先生成图像", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 将点击的符号添加到输入框
    private void appendSymbol(String symbol) {
        inputStringBuilder.append(symbol);
        inputText.setText(inputStringBuilder.toString());
    }

    // 删除输入框中的最后一个符号
    private void deleteLastSymbol() {
        if (inputStringBuilder.length() > 0) {
            // 从字符串的末尾开始向前查找符号
            int startIndex = inputStringBuilder.length() - 1;

            // 获取当前字符的长度，判断它是否是一个完整的符号
            int charLength = Character.charCount(inputStringBuilder.charAt(startIndex));

            // 如果符号长度大于1，说明它是一个多字符的符号
            // 删除符号
            inputStringBuilder.delete(startIndex - charLength + 1, startIndex + 1);

            // 更新输入框显示
            inputText.setText(inputStringBuilder.toString());
        }
    }






    // 生成图案的Bitmap


    // 生成8x32的图片
    private void generateImage() {

        int width = 32;  // 图像宽度
        int height = 8;  // 图像高度
        String inputString = inputStringBuilder.toString();
        if (inputString.isEmpty()) {
            Toast.makeText(this, "请输入音乐符号", Toast.LENGTH_SHORT).show();
            return;
        }

        // 创建一个32x8的空白图案
        Bitmap finalBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(finalBitmap);
        String text = inputText.getText().toString();

        // 创建画笔
        Paint paint = new Paint();
        paint.setColor(Color.RED);  // 设置文字颜色为红色
        paint.setTextSize(8);  // 设置字体大小为8
        paint.setTextAlign(Paint.Align.LEFT);  // 文字左对齐

        // 设置背景为黑色
        canvas.drawColor(Color.BLACK);

        // 在画布上绘制文字
        canvas.drawText(text, 0, height - 2, paint);  // 设置文字位置



        // 设置生成的图像显示在 ImageView 中
        generatedImageView.setImageBitmap(finalBitmap);
        generatedBitmap=finalBitmap;
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
            resource.saveBitmapToFile(resource.toBitmap(),System.currentTimeMillis()+"", getFilesDir());
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
