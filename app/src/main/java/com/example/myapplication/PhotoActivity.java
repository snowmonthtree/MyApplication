package com.example.myapplication;

import static android.os.Environment.getExternalStoragePublicDirectory;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;

public class PhotoActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CAMERA = 100;
    private static final int REQUEST_CODE_PERMISSIONS = 101;
    private ImageView imageView;
    private Button takePictureButton;
    private Button saveButton;

    private LEDResource resource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        imageView = findViewById(R.id.imageView);
        takePictureButton = findViewById(R.id.selectImageButton);
        saveButton = findViewById(R.id.saveButton);

        // 拍摄图片按钮点击事件
        takePictureButton.setOnClickListener(view -> {
            // 检查是否已授予相机权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                // 已授权，可以执行拍照
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA);
            } else {
                // 如果没有权限，请求权限
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_CODE_PERMISSIONS);
            }
        });

        // 保存资源按钮点击事件
        saveButton.setOnClickListener(view -> save());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 如果是拍照完成
        if (requestCode == REQUEST_CODE_CAMERA && resultCode == RESULT_OK) {
            // 获取拍摄的图片
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");

            // 将图片压缩为 8x32 像素的大小
            Bitmap compressedBitmap = Bitmap.createScaledBitmap(imageBitmap, 32, 8, true);

            // 存储该压缩图片为LED资源
            resource = new LEDResource(32, 8);
            for (int y = 0; y < 8; y++) {
                for (int x = 0; x < 32; x++) {
                    int pixelColor = compressedBitmap.getPixel(x, y);
                    resource.setColor(x, y, pixelColor);
                }
            }

            // 设置压缩后的图片显示在 ImageView 中
            imageView.setImageBitmap(compressedBitmap);
        }
    }

    // 保存资源方法
    public void save(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // 如果没有权限，请求权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    100);
        }
        try {
            resource.saveBitmapToFile(resource.toBitmap(),System.currentTimeMillis()+"",getFilesDir());
            /*MediaScannerConnection.scanFile(this,
                    new String[]{getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()},
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

    // 请求权限回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "权限已授予", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "权限被拒绝，无法使用相机", Toast.LENGTH_SHORT).show();
            }
        }
        else if(requestCode == 100){
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


