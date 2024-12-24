package com.example.myapplication;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifDrawableBuilder;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class AnimationActivity extends AppCompatActivity {

    private static final int PICK_GIF_REQUEST = 1;

    private ImageView gifImageView;
    private Button selectGifButton;
    private Button saveButton;

    private final List<Bitmap> resizedFrames = new ArrayList<>(); // 保存缩放后的帧

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);

        gifImageView = findViewById(R.id.gifImageView);
        selectGifButton = findViewById(R.id.selectGifButton);
        saveButton = findViewById(R.id.saveButton);

        selectGifButton.setOnClickListener(v -> openGifPicker());
        saveButton.setOnClickListener(v -> createAndSaveGif());
    }

    // 打开图库选择动图
    private void openGifPicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_GIF_REQUEST);
    }

    // 处理选择的动图，缩放为 8x32 并显示
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_GIF_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedGifUri = data.getData();
            Log.d("GIF_DEBUG", "Selected GIF URI: " + selectedGifUri);
            if (selectedGifUri != null) {
                processGif(selectedGifUri); // 使用 pl.droidsonroids.gif 处理 GIF
            } else {
                Toast.makeText(this, "未选择动图", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 使用 pl.droidsonroids.gif 处理 GIF 并缩放帧
    private void processGif(Uri gifUri) {
        try {
            // 从 URI 获取输入流
            BufferedInputStream inputStream = new BufferedInputStream(getContentResolver().openInputStream(gifUri));

            // 使用 GifDrawable 处理 GIF
            GifDrawable gifDrawable = new GifDrawableBuilder().from(inputStream).build();
            resizedFrames.clear(); // 清空帧列表

            // 提取并缩放帧
            for (int i = 0; i < gifDrawable.getNumberOfFrames(); i++) {
                Bitmap frame = gifDrawable.seekToFrameAndGet(i);
                Bitmap resizedFrame = Bitmap.createScaledBitmap(frame, 32, 8, false);
                resizedFrames.add(resizedFrame);
            }

            Toast.makeText(this, "GIF 已成功处理并缩放为 8x32", Toast.LENGTH_SHORT).show();
            displayResizedAnimation(); // 显示动画

            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("GIF_DEBUG", "Error processing GIF: " + e.getMessage());
            Toast.makeText(this, "GIF 处理失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // 显示缩放后的帧作为动画
    private void displayResizedAnimation() {
        if (resizedFrames.isEmpty()) {
            Toast.makeText(this, "没有帧可显示", Toast.LENGTH_SHORT).show();
            return;
        }

        // 创建动画 Drawable
        AnimationDrawable animationDrawable = new AnimationDrawable();
        for (Bitmap frame : resizedFrames) {
            animationDrawable.addFrame(new BitmapDrawable(getResources(), frame), 100); // 每帧持续 100ms
        }
        animationDrawable.setOneShot(false); // 循环播放

        // 设置到 ImageView
        gifImageView.setImageDrawable(animationDrawable);
        animationDrawable.start();
    }


    // 创建并保存 GIF 文件
    private void createAndSaveGif() {
        if (resizedFrames.isEmpty()) {
            Toast.makeText(this, "没有帧可保存", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String fileName = "resized_animation_" + System.currentTimeMillis() + ".gif";
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "image/gif");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/MyAppGifs");

           /* Uri gifUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (gifUri == null) {
                Toast.makeText(this, "无法创建文件", Toast.LENGTH_SHORT).show();
                return;
            }*/

            List<Bitmap> framesToSave = new ArrayList<>();
            for (Bitmap frame : resizedFrames) {
                framesToSave.add(frame.copy(frame.getConfig(), true)); // 深拷贝
            }
            File fileDir = getFilesDir();  // 获取私有目录
            File gifFile = new File(fileDir, fileName);  // 在私有目录下创建一个新的文件
           // try (OutputStream outputStream = getContentResolver().openOutputStream(gifUri)) {
            try (OutputStream outputStream = new FileOutputStream(gifFile)) {
                AnimatedGifEncoder gifEncoder = new AnimatedGifEncoder();
                gifEncoder.start(outputStream);
                gifEncoder.setRepeat(0);
                gifEncoder.setDelay(100);

                for (Bitmap frame : framesToSave) {
                    if (frame.isRecycled()) {
                        Log.e("GIF_SAVE_ERROR", "Bitmap 已被回收，跳过该帧");
                        continue;
                    }
                    gifEncoder.addFrame(frame);
                }

                gifEncoder.finish();
                Toast.makeText(this, "GIF 已保存到相册: " + fileName, Toast.LENGTH_LONG).show();
                new AlertDialog.Builder(this)
                        .setTitle("成功")
                        .setMessage("新生成的图像已保存,如果要上传,请到创作中心->本地资源->选择相应的图片上传")
                        .setPositiveButton("确定", (dialog, which) -> {
                            // 确定按钮的点击事件
                        })
                        .show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "保存失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
