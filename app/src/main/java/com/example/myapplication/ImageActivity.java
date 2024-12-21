package com.example.myapplication;

import static android.os.Environment.getExternalStoragePublicDirectory;

import android.Manifest;
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

public class ImageActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageView;
    private Button selectImageButton;
    private Button saveButton;

    private LEDResource resource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        imageView = findViewById(R.id.imageView);
        selectImageButton = findViewById(R.id.selectImageButton);
        saveButton = findViewById(R.id.saveButton);

        selectImageButton.setOnClickListener(v -> openImagePicker());
        saveButton.setOnClickListener(v -> save());
    }

    // 打开图库选择图片
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // 处理选择的图片，压缩并显示
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            try {
                // 获取图片的URI
                android.net.Uri selectedImageUri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);

                // 压缩图片为8x32尺寸
                Bitmap compressedBitmap = Bitmap.createScaledBitmap(bitmap, 32, 8, false);
                imageView.setImageBitmap(compressedBitmap);

                // 存储该压缩图片为LED资源
                resource = new LEDResource(32, 8);
                for (int y = 0; y < 8; y++) {
                    for (int x = 0; x < 32; x++) {
                        int pixelColor = compressedBitmap.getPixel(x, y);
                        resource.setColor(x, y, pixelColor);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "图片加载失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 保存图片资源
    public void save(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // 如果没有权限，请求权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    100);
        }
        try {
            resource.saveBitmapToFile(resource.toBitmap(),System.currentTimeMillis()+"",getFilesDir());
            /*MediaScannerConnection.scanFile(this,
                    new String[]{getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()},
                    null,
                    (path, uri) -> Log.d("MediaScanner", "File scanned: " + path));
*/
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Toast.makeText(this, "LED Resource Saved:\n" + resource.toString(), Toast.LENGTH_SHORT).show();
    }



    // 权限请求结果回调
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
