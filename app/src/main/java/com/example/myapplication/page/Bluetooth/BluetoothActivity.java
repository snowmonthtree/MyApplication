package com.example.myapplication.page.Bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.graphics.drawable.BitmapDrawable;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.graphics.BitmapFactory;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.myapplication.Controller.LedListController;
import com.example.myapplication.Controller.LedResourceController;
import com.example.myapplication.R;
import com.example.myapplication.RetrofitClient;
import com.example.myapplication.data.LedResource.LedResource;
import com.example.myapplication.data.ViewSharer;
import com.example.myapplication.page.Video.PlayVideoActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BluetoothActivity extends AppCompatActivity {
    private static final String TAG = "BluetoothActivity";

    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;

    private final String DEVICE_NAME = "zhetu"; // 目标蓝牙模块名称
    private final UUID DEVICE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // 通用串口 UUID

    private Button btnConnect, btnSend;
    private ImageView imageView;
    private Button btnTest;
    private ViewSharer viewSharer;
    private LedListController ledListController;
    private LedResourceController ledResourceController;
    private Retrofit retrofit;
    private String resourceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        btnConnect = findViewById(R.id.btnConnect);
        btnSend = findViewById(R.id.btnSend);
        imageView = findViewById(R.id.imageView);
        btnTest = findViewById(R.id.btntest);
        viewSharer=(ViewSharer)getApplication();
        try {
            retrofit= RetrofitClient.getClient(this);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        ledListController=retrofit.create(LedListController.class);
        ledResourceController=retrofit.create(LedResourceController.class);
        btnTest.setOnClickListener(view -> test());


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // 设置按钮点击事件
        btnConnect.setOnClickListener(v -> checkAndRequestPermissions());
        btnSend.setOnClickListener(v -> sendImageData());



        Intent intent = getIntent();
        resourceId= intent.getStringExtra("image");
        if (resourceId!= null) {
            Call<LedResource> call=ledResourceController.getResourceById(resourceId);
            call.enqueue(new Callback<LedResource>() {
                @Override
                public void onResponse(Call<LedResource> call, Response<LedResource> response) {
                    if (response.body()!=null&&response.isSuccessful()){
                        fetchImage(response.body().getViewWebUrl(),imageView);
                    }
                }

                @Override
                public void onFailure(Call<LedResource> call, Throwable t) {

                }
            });
        }
        else {
            Uri uri=intent.getParcelableExtra("uri");
            imageView.setImageURI(uri);
            btnTest.setVisibility(View.GONE);
        }

    }

    /**
     * 检查并请求蓝牙相关权限
     */
    private void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN},
                    REQUEST_BLUETOOTH_PERMISSIONS
            );
        } else {
            connectToBluetoothDevice();
        }
    }

    /**
     * 权限请求结果回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                connectToBluetoothDevice();
            } else {
                Toast.makeText(this, "蓝牙权限被拒绝，无法继续操作", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 连接到蓝牙设备
     */
    private void connectToBluetoothDevice() {
        if (bluetoothAdapter == null) {
            showToast("设备不支持蓝牙");
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            showToast("请先打开蓝牙");
            return;
        }

        BluetoothDevice targetDevice = null;

        try {
            // 查找目标蓝牙设备
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            for (BluetoothDevice device : pairedDevices) {
                if (DEVICE_NAME.equals(device.getName())) {
                    targetDevice = device;
                    break;
                }
            }
        } catch (SecurityException e) {
            Log.e(TAG, "获取配对设备失败：缺少权限", e);
            showToast("获取配对设备失败，请检查权限");
            return;
        }

        if (targetDevice == null) {
            showToast("未找到目标蓝牙设备");
            return;
        }

        try {
            // 尝试连接蓝牙设备
            bluetoothSocket = targetDevice.createRfcommSocketToServiceRecord(DEVICE_UUID);
            bluetoothSocket.connect();
            outputStream = bluetoothSocket.getOutputStream();
            showToast("蓝牙连接成功");
        } catch (SecurityException e) {
            Log.e(TAG, "连接蓝牙设备失败：缺少权限", e);
            showToast("蓝牙连接失败，请检查权限");
        } catch (IOException e) {
            Log.e(TAG, "蓝牙连接失败", e);
            showToast("蓝牙连接失败");
        }
    }

    /**
     * 发送图片数据
     */
    private void sendImageData() {

        if (outputStream == null) {
            showToast("蓝牙未连接");
            return;
        }


        // 将 ImageView 的图片转换为 Bitmap
       // imageView.setDrawingCacheEnabled(true);

        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        //bitmap = Bitmap.createScaledBitmap(bitmap, 8, 32, false);
        imageView.setImageBitmap(bitmap);
      // Bitmap bitmap = imageView.getDrawingCache();

        if (bitmap == null) {
            showToast("图片为空");
            return;
        }

        byte[] imageData = convertImageToBytes(bitmap);

        // 在发送前将所有 0xFF 替换为 0xFE，避免冲突
        imageData = replaceByteValue(imageData, (byte) 0xFF, (byte) 0xFE);



        //调试，将图片的发送数据打印到logcat
        logByteArray(imageData);

        try {
            outputStream.write(imageData);
            showToast("图片数据已发送");
        } catch (IOException e) {
            Log.e(TAG, "发送失败", e);
            showToast("图片发送失败");
        } finally {
            imageView.setDrawingCacheEnabled(false);
        }
    }

    // 输出字节数组到 Logcat
    private void logByteArray(byte[] byteArray) {
        StringBuilder stringBuilder = new StringBuilder();
        int lineLength = 32 * 3; // 每行32个像素，每个像素3个字节

       for (int i = 0; i < byteArray.length; i++) {
            stringBuilder.append(String.format("%02X ", byteArray[i])); // 打印每个字节的16进制表示


            // 每输出32个像素，换行
            if ((i + 1) % lineLength == 0) {
                Log.d(TAG, "发送的数据: " + stringBuilder.toString());
                stringBuilder.setLength(0); // 清空 StringBuilder，准备下一行
            }



        }
        if (stringBuilder.length() > 0) {
            Log.d(TAG, "发送的数据: " + stringBuilder.toString());
        }



    }

    /**
     * 将图片转换为字节数组（每个像素3个字节，RGB）
     */
    private byte[] convertImageToBytes(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Log.d(TAG, "图像的宽度为：" + width);
        Log.d(TAG, "图像的高度为：" + height);

        // 计算字节数组的大小，每个像素使用3个字节（RGB）
        byte[] byteArray = new byte[width * height * 3 + 1]; // 多一个字节用于结束标志

        int index = 0;

        // 填充RGB值
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = bitmap.getPixel(x, y);

                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);

                byteArray[index++] = (byte) red;
                byteArray[index++] = (byte) green;
                byteArray[index++] = (byte) blue;
            }
        }

        // 添加结束标志字节（0xFF）
        byteArray[byteArray.length - 1] = (byte) 0xFF;

        return byteArray;
    }

    /**
     * 替换字节数组中的某个字节值
     */
    private byte[] replaceByteValue(byte[] byteArray, byte oldValue, byte newValue) {
        for (int i = 0; i < byteArray.length-1; i++) {
            if (byteArray[i] == oldValue) {
                byteArray[i] = newValue;
            }
        }
        return byteArray;
    }

    /**
     * 显示提示信息
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    private void test(){
        if (viewSharer.getListId()!=null){
            Call<String> call=ledListController.addResourceToPlaylist(viewSharer.getUser().getUserId(),viewSharer.getListId(),resourceId);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Toast.makeText(BluetoothActivity.this, response.body(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(BluetoothActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toast.makeText(viewSharer, "未设置播放序列", Toast.LENGTH_SHORT).show();
        }
    }
    private byte[] readBytesFromStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, length);
        }
        return byteArrayOutputStream.toByteArray();  // 返回字节数组
    }
    // 判断是否是 GIF 格式
    private boolean isGif(byte[] imageData) {
        if (imageData.length >= 4) {
            return imageData[0] == 'G' && imageData[1] == 'I' && imageData[2] == 'F';
        }
        return false;
    }
    // 将ResponseBody转换为字节数组
    private byte[] convertResponseBodyToBytes(ResponseBody responseBody) {
        byte[] data = null;
        InputStream inputStream = responseBody.byteStream();
        try {
            data = readBytesFromStream(inputStream);  // 读取字节流到字节数组
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();  // 关闭输入流
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }
    private void fetchImage(String viewWebUrl, ImageView imageView) {
        // 假设 ledResourceController 是 Retrofit 接口，调用 getImage 方法来获取图片
        ledResourceController.getImage(viewWebUrl).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        ResponseBody responseBody = response.body();
                        byte[] imageData = convertResponseBodyToBytes(responseBody);  // 获取字节数组

                        // 读取字节流并判断是否为 GIF 格式
                        if (isGif(imageData)) {
                            // 如果是 GIF 动图
                            Glide.with(BluetoothActivity.this)
                                    .asGif()
                                    .load(imageData)  // 加载字节数组
                                    .placeholder(R.drawable.ic_caution_refresh)
                                    .error(R.drawable.ic_doodle_back)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(imageView);  // 显示 GIF 动图
                        } else {
                            // 如果是静态图片（如 PNG, JPEG）
                            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                            if (bitmap != null) {
                                Glide.with(BluetoothActivity.this)
                                        .load(imageData)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .placeholder(R.drawable.ic_caution_refresh)
                                        .error(R.drawable.ic_doodle_back)
                                        .into(imageView);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("Image Fetch", "Failed to process response", e);
                    }
                } else {
                    Log.e("Image Fetch", "onResponse: Image fetch failed");
                }
            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // 网络请求失败
                Log.e("Image Fetch", "onFailure: Image fetch failed", t);
            }
        });
    }
}
