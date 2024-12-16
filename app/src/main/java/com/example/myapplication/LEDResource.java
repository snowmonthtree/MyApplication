package com.example.myapplication;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class LEDResource {
    private int width;
    private int height;
    private int[][] colors;

    public LEDResource(int width, int height) {
        this.width = width;
        this.height = height;
        colors = new int[height][width];
    }

    public void setColor(int x, int y, int color) {
        colors[y][x] = color;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                sb.append(String.format("#%06X ", colors[y][x]));
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    public void saveBitmapToFile(Bitmap bitmap,String id,File directory) throws IOException {
        // 获取存储路径，这里使用外部存储目录
        if (!directory.exists()) {
            directory.mkdirs(); // 创建目录
        }

        // 创建文件
        File file = new File(directory, id+".png");
        FileOutputStream outStream = new FileOutputStream(file);

        // 将 Bitmap 保存为 PNG 格式
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);

        // 关闭文件输出流
        outStream.flush();
        outStream.close();
    }
    public Bitmap toBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                bitmap.setPixel(x, y, colors[y][x]);
            }
        }
        return bitmap;
    }
}
