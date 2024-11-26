package com.example.myapplication;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class CreationCenterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation_center_passenger);

        // 初始化按钮
        Button buttonDoodle = findViewById(R.id.button_doodle);
        Button buttonAnimation = findViewById(R.id.button_animation);
        Button buttonMusic = findViewById(R.id.button_music);
        Button buttonImage = findViewById(R.id.button_image);
        Button buttonPhoto = findViewById(R.id.button_photo);
        Button buttonText = findViewById(R.id.button_text);

        // 设置点击监听器
       /* buttonDoodle.setOnClickListener(v -> {
            Intent intent = new Intent(CreationCenterActivity.this, DoodleActivity.class);
            startActivity(intent);
        });

        buttonAnimation.setOnClickListener(v -> {
            Intent intent = new Intent(CreationCenterActivity.this, AnimationActivity.class);
            startActivity(intent);
        });

        buttonMusic.setOnClickListener(v -> {
            Intent intent = new Intent(CreationCenterActivity.this, MusicActivity.class);
            startActivity(intent);
        });

        buttonImage.setOnClickListener(v -> {
            Intent intent = new Intent(CreationCenterActivity.this, ImageActivity.class);
            startActivity(intent);
        });

        buttonPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(CreationCenterActivity.this, PhotoActivity.class);
            startActivity(intent);
        });

        buttonText.setOnClickListener(v -> {
            Intent intent = new Intent(CreationCenterActivity.this, TextActivity.class);
            startActivity(intent);
        });*/
    }
}