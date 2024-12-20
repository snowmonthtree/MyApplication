package com.example.myapplication.page.Profile.Setting;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS = 2;
    private SwitchMaterial bluetoothSwitch;
    private BluetoothAdapter bluetoothAdapter;
    private ActivityResultLauncher<Intent> enableBtLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // 初始化蓝牙适配器
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // 初始化滑块控件
        bluetoothSwitch = findViewById(R.id.bluetoothSwitch);

        // 注册 Activity Result Launcher
        enableBtLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Toast.makeText(this, "蓝牙已开启", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "蓝牙未开启", Toast.LENGTH_SHORT).show();
                        bluetoothSwitch.setChecked(false);
                    }
                }
        );

        // 设置滑块的点击监听器
        bluetoothSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // 开启蓝牙
                if (hasBluetoothPermissions()) {
                    try {
                        if (!bluetoothAdapter.isEnabled()) {
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            enableBtLauncher.launch(enableBtIntent);
                        }
                    } catch (SecurityException e) {
                        Toast.makeText(this, "权限被拒绝，无法开启蓝牙", Toast.LENGTH_SHORT).show();
                        bluetoothSwitch.setChecked(false);
                    }
                } else {
                    requestBluetoothPermissions();
                }
            } else {
                // 关闭蓝牙
                if (hasBluetoothPermissions()) {
                    try {
                        if (bluetoothAdapter.isEnabled()) {
                            bluetoothAdapter.disable();
                        }
                    } catch (SecurityException e) {
                        Toast.makeText(this, "权限被拒绝，无法关闭蓝牙", Toast.LENGTH_SHORT).show();
                        bluetoothSwitch.setChecked(true);
                    }
                } else {
                    Toast.makeText(this, "权限被拒绝，无法关闭蓝牙", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 初始化滑块的状态
        if (hasBluetoothPermissions()) {
            bluetoothSwitch.setChecked(bluetoothAdapter.isEnabled());
        } else {
            bluetoothSwitch.setChecked(false);
            Toast.makeText(this, "权限被拒绝，无法读取蓝牙状态", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean hasBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
        }
        return true; // 对于低于 API 31 的设备，不需要 BLUETOOTH_CONNECT 权限
    }

    private void requestBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限被授予
                bluetoothSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        try {
                            if (!bluetoothAdapter.isEnabled()) {
                                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                enableBtLauncher.launch(enableBtIntent);
                            }
                        } catch (SecurityException e) {
                            Toast.makeText(this, "权限被拒绝，无法开启蓝牙", Toast.LENGTH_SHORT).show();
                            bluetoothSwitch.setChecked(false);
                        }
                    } else {
                        try {
                            if (bluetoothAdapter.isEnabled()) {
                                bluetoothAdapter.disable();
                            }
                        } catch (SecurityException e) {
                            Toast.makeText(this, "权限被拒绝，无法关闭蓝牙", Toast.LENGTH_SHORT).show();
                            bluetoothSwitch.setChecked(true);
                        }
                    }
                });

                // 初始化滑块的状态
                bluetoothSwitch.setChecked(bluetoothAdapter.isEnabled());
            } else {
                // 权限被拒绝
                Toast.makeText(this, "权限被拒绝，无法操作蓝牙", Toast.LENGTH_SHORT).show();
                bluetoothSwitch.setChecked(false);
            }
        }
    }
}