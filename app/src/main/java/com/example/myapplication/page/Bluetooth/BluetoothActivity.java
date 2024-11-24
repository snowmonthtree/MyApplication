package com.example.myapplication.page.Bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.R;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity {
    private static final String TAG = "BluetoothActivity";

    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;

    private final String DEVICE_NAME = "HC-05"; // 目标蓝牙模块名称
    private final UUID DEVICE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // 通用串口 UUID

    private Button btnConnect, btnSend;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        btnConnect = findViewById(R.id.btnConnect);
        btnSend = findViewById(R.id.btnSend);
        imageView = findViewById(R.id.imageView);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // 设置按钮点击事件
        btnConnect.setOnClickListener(v -> checkAndRequestPermissions());
        btnSend.setOnClickListener(v -> sendImageData());
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
        imageView.setDrawingCacheEnabled(true);
        Bitmap bitmap = imageView.getDrawingCache();

        if (bitmap == null) {
            showToast("图片为空");
            return;
        }

        String compressedData = compressImage(bitmap);

        try {
            outputStream.write((compressedData + "\n").getBytes());
            showToast("图片数据已发送");
        } catch (IOException e) {
            Log.e(TAG, "发送失败", e);
            showToast("图片发送失败");
        } finally {
            imageView.setDrawingCacheEnabled(false);
        }
    }

    /**
     * 压缩图片为数据格式
     */
    private String compressImage(Bitmap bitmap) {
        StringBuilder data = new StringBuilder();

        for (int y = 0; y < bitmap.getHeight(); y++) {
            for (int x = 0; x < bitmap.getWidth(); x++) {
                int pixel = bitmap.getPixel(x, y);

                // 获取 RGB 颜色值
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);

                // 将颜色压缩为 16 进制格式
                String color = String.format("%02X%02X%02X", red, green, blue);

                // 每个像素点存储为 "颜色|数量;"
                data.append(color).append("|1;");
            }
        }

        return data.toString();
    }

    /**
     * 显示提示信息
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
