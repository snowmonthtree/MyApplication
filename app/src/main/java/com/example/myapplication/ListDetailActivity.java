package com.example.myapplication;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.myapplication.Controller.LedListController;
import com.example.myapplication.Controller.LedResourceController;
import com.example.myapplication.data.LedResource.LedResource;
import com.example.myapplication.data.Result.ResultItem;
import com.example.myapplication.data.ViewSharer;
import com.example.myapplication.page.Bluetooth.BluetoothActivity;
import com.example.myapplication.ui.LedListAdapter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import okhttp3.ResponseBody;
import pl.droidsonroids.gif.GifDrawable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ListDetailActivity extends AppCompatActivity {
    private Retrofit retrofit;
    private LedListController ledListController;
    private LedResourceController ledResourceController;
    private RecyclerView recyclerView;
    private LedListAdapter ledListAdapter;
    private ViewSharer viewSharer;
    private String listId;
    private Button setList;
    private Button showList;
    private Button deleteList;
    private static final String TAG = "BluetoothActivity2";
    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;

    private final String DEVICE_NAME = "HC-05"; // 目标蓝牙模块名称
    private final UUID DEVICE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // 通用串口 UUID

    private BluetoothAnimationSender bluetoothAnimationSender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        try {
            retrofit = RetrofitClient.getClient(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Intent intent = getIntent();
        listId = intent.getStringExtra("listId");
        viewSharer = (ViewSharer) getApplication();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        ledListController = retrofit.create(LedListController.class);
        ledResourceController = retrofit.create(LedResourceController.class);
        deleteList = findViewById(R.id.deleteList);
        setList = findViewById(R.id.setAsNow);
        showList=findViewById(R.id.showList);
        recyclerView = findViewById(R.id.recycler_list_detail);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ledListAdapter = new LedListAdapter(new ArrayList<ResultItem>(), ledResourceController, ledListController, viewSharer, this); // 先传入一个空列表
        recyclerView.setAdapter(ledListAdapter);
        initList();
        setList.setOnClickListener(view -> setList());
        deleteList.setOnClickListener(view -> deleteList());
        showList.setOnClickListener(view -> sendImageData());
    }

    private void initList() {
        Call<List<LedResource>> call = ledListController.getPlaylistResources(viewSharer.getUser().getUserId(), listId);
        call.enqueue(new Callback<List<LedResource>>() {
            @Override
            public void onResponse(Call<List<LedResource>> call, Response<List<LedResource>> response) {
                List<ResultItem> list = new ArrayList<ResultItem>();
                for (LedResource ledResource : response.body()) {
                    list.add(new ResultItem(ledResource.getName(), ledResource.getDetail(), ledResource.getViewWebUrl(), ledResource.getResourceId()));

                }
                ledListAdapter.updateData(list, listId);
            }

            @Override
            public void onFailure(Call<List<LedResource>> call, Throwable t) {

            }
        });
    }

    private void setList() {
        viewSharer.setListId(listId);
        Toast.makeText(this, "列表已设置", Toast.LENGTH_SHORT).show();
    }

    private void deleteList() {
        Call<String> call = ledListController.deletePlaylist(viewSharer.getUser().getUserId(), listId);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Toast.makeText(ListDetailActivity.this, response.body(), Toast.LENGTH_SHORT).show();
                finish();
            }


            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(ListDetailActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{android.Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN},
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
            bluetoothAnimationSender = new BluetoothAnimationSender(bluetoothSocket);
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
       checkAndRequestPermissions();
        if (outputStream == null) {
            showToast("蓝牙未连接");
            return;
        }
        System.out.println(ledListAdapter.getListToDisplay(recyclerView));
        try {
            bluetoothAnimationSender.sendAnimationFrames(ledListAdapter.getListToDisplay(recyclerView),100);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void showToast(String text) {
        Toast.makeText(this, " " + text, Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放资源
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}