package com.example.myapplication;

import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class BluetoothAnimationSender {
    private static final String TAG = "BluetoothAnimationSender";
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;

    public BluetoothAnimationSender(BluetoothSocket socket) {
        this.bluetoothSocket = socket;
        try {
            this.outputStream = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "获取输出流失败", e);
        }
    }

    /**
     * 发送动图帧列表，通过蓝牙逐帧发送图片实现动画效果
     *
     * @param bitmapList   图片帧列表
     * @param frameDelayMs 每帧的延迟时间（毫秒）
     */
    public void sendAnimationFrames(List<Bitmap> bitmapList, int frameDelayMs) {
        if (outputStream == null) {
            Log.e(TAG, "输出流未初始化，蓝牙未连接");
            return;
        }

        for (int i = 0; i < bitmapList.size(); i++) {
            Bitmap bitmap = bitmapList.get(i);
            if (bitmap == null) {
                Log.e(TAG, "第 " + i + " 帧为空，跳过该帧");
                continue;
            }

            try {
                // 将帧数据转换为字节数组
                byte[] frameData = convertImageToBytes(bitmap);

                // 在发送前将所有 0xFF 替换为 0xFE，避免冲突
                frameData = replaceByteValue(frameData, (byte) 0xFF, (byte) 0xFE);

                // 调试用：打印帧数据到 Logcat
                logByteArray(frameData);

                // 发送帧数据
                outputStream.write(frameData);
                outputStream.flush();
                Log.d(TAG, "第 " + (i + 1) + " 帧发送成功");

                // 等待帧间隔时间
                Thread.sleep(frameDelayMs);
            } catch (IOException e) {
                Log.e(TAG, "发送帧失败", e);
            } catch (InterruptedException e) {
                Log.e(TAG, "帧延迟被中断", e);
            }
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
        for (int i = 0; i < byteArray.length - 1; i++) {
            if (byteArray[i] == oldValue) {
                byteArray[i] = newValue;
            }
        }
        return byteArray;
    }

    /**
     * 输出字节数组到 Logcat
     */
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
}
