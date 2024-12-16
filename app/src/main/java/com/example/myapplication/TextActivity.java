package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
                    LEDResource resource = saveAsLEDResource(generatedBitmap);
                    // 显示保存成功的消息
                    Toast.makeText(TextActivity.this, "图像已保存为LED资源", Toast.LENGTH_SHORT).show();
                    // 可以进一步显示LED资源的内容，或者进行其他操作
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

    // 将生成的图像保存为LED资源
    private LEDResource saveAsLEDResource(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        LEDResource resource = new LEDResource(width, height);

        // 将图像的每个像素转为LED资源的颜色
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int color = bitmap.getPixel(x, y);
                resource.setColor(x, y, color);
            }
        }
        return resource;
    }
}
