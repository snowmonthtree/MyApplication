package com.example.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.Stack;

public class DrawingView extends View {

    private Paint paint;
    private int matrixWidth = 32;   // 画布宽度：32个方块
    private int matrixHeight = 8;   // 画布高度：8个方块
    private float blockSize;  // 方块的大小
    private int[][] colorMatrix;
    private int currentColor = Color.RED;

    // 用于实现撤销功能
    private Stack<int[][]> undoStack;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(currentColor);
        colorMatrix = new int[matrixHeight][matrixWidth];
        undoStack = new Stack<>();
    }

    // 设置画笔颜色
    public void setPaintColor(int color) {
        currentColor = color;
        paint.setColor(color);
    }

    // 清空画布
    public void clearCanvas() {
        saveStateToUndoStack();  // 保存当前状态到撤销栈
        colorMatrix = new int[matrixHeight][matrixWidth];
        invalidate();
    }

    // 撤销操作
    public void undoLastAction() {
        if (!undoStack.isEmpty()) {
            colorMatrix = undoStack.pop();
            invalidate();
        }
    }

    // 将当前绘制的颜色矩阵保存为LED资源
    public LEDResource saveAsLEDResource() {
        LEDResource resource = new LEDResource(matrixWidth, matrixHeight);
        for (int y = 0; y < matrixHeight; y++) {
            for (int x = 0; x < matrixWidth; x++) {
                resource.setColor(x, y, colorMatrix[y][x]);
            }
        }
        return resource;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 确保每个方框是正方形，取较小的宽高值来决定方框大小
        blockSize = Math.min(w / (float) matrixWidth, h / (float) matrixHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 计算画布中间的偏移量，确保画布居中显示
        float offsetX = (getWidth() - matrixWidth * blockSize) / 2;
        float offsetY = (getHeight() - matrixHeight * blockSize) / 2;

        // 画紫色方框，框住8x32的小方格
        paint.setColor(Color.parseColor("#800080"));  // 紫色
        paint.setStyle(Paint.Style.STROKE);  // 只画边框
        paint.setStrokeWidth(5);  // 设置边框宽度
        canvas.drawRect(
                offsetX,
                offsetY,
                offsetX + matrixWidth * blockSize,
                offsetY + matrixHeight * blockSize,
                paint
        );

        // 画出每个小方格
        paint.setStyle(Paint.Style.FILL);  // 重新设置为填充样式
        for (int y = 0; y < matrixHeight; y++) {
            for (int x = 0; x < matrixWidth; x++) {
                paint.setColor(colorMatrix[y][x]);
                canvas.drawRect(
                        offsetX + x * blockSize,  // 计算方块的绘制位置
                        offsetY + y * blockSize,  // 计算方块的绘制位置
                        offsetX + (x + 1) * blockSize,
                        offsetY + (y + 1) * blockSize,
                        paint
                );
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        // 判断触摸点落在的方块坐标
        int gridX = (int) ((x - (getWidth() - matrixWidth * blockSize) / 2) / blockSize);
        int gridY = (int) ((y - (getHeight() - matrixHeight * blockSize) / 2) / blockSize);

        if (gridX >= 0 && gridX < matrixWidth && gridY >= 0 && gridY < matrixHeight) {
            // 在触摸前保存当前状态到撤销栈
            saveStateToUndoStack();
            // 更新颜色矩阵
            colorMatrix[gridY][gridX] = currentColor;
            invalidate();
        }

        return true;
    }

    // 保存当前状态到撤销栈
    private void saveStateToUndoStack() {
        // 复制当前颜色矩阵到栈中，确保栈中的状态是矩阵的副本
        int[][] matrixCopy = new int[matrixHeight][matrixWidth];
        for (int y = 0; y < matrixHeight; y++) {
            System.arraycopy(colorMatrix[y], 0, matrixCopy[y], 0, matrixWidth);
        }
        undoStack.push(matrixCopy);
    }
}
