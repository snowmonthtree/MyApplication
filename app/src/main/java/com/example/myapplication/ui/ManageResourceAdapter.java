package com.example.myapplication.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.myapplication.Controller.LedResourceController;
import com.example.myapplication.R;
import com.example.myapplication.data.LedResource.LedResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageResourceAdapter extends RecyclerView.Adapter<ManageResourceAdapter.LedResourceViewHolder> {
    private List<LedResource> list;  // 数据源
    private LedResourceController ledResourceController;  // 控制器用于与服务器或数据库交互
    private Context context;  // 上下文，用于显示UI元素（如Toast、对话框等）

    // 构造器
    public ManageResourceAdapter(List<LedResource> list, LedResourceController ledResourceController, Context context) {
        this.list = list;
        this.ledResourceController = ledResourceController;
        this.context = context;
    }

    @NonNull
    @Override
    public LedResourceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 创建视图对象并返回
        View view = LayoutInflater.from(context).inflate(R.layout.list_manage_resource_layout, parent, false);
        return new LedResourceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LedResourceViewHolder holder, int position) {
        // 获取当前资源
        LedResource ledResource = list.get(position);

        // 设置显示的数据
        holder.nameTextView.setText(ledResource.getName());
        holder.resourceTextView.setText(ledResource.getName());
        fetchImage(ledResource.getViewWebUrl(),holder.resourceIv);
        // 设置点击删除按钮的监听器
        holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("删除资源?")
                        .setMessage("该操作无法撤销")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", (dialog, which) -> {
                            // 调用控制器删除资源
                            Call<String> call=ledResourceController.deleteResource(ledResource.getUserId(),ledResource.getResourceId());
                            call.enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    if (response.isSuccessful()) {

                                        Toast.makeText(context, "资源删除成功", Toast.LENGTH_SHORT).show();
                                        list.remove(holder.getAdapterPosition());
                                        notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Toast.makeText(context, "网络请求失败", Toast.LENGTH_SHORT).show();
                                }
                            });

                        })
                        .show();
            }
        });
    }
    public void updateData(List<LedResource> newData) {
        list.clear();
        list.addAll(newData);
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return list.size();  // 返回数据集的大小
    }

    // ViewHolder类，用来绑定视图元素
    public static class LedResourceViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView resourceTextView;
        ImageView resourceIv;
        ImageView deleteImageView;  // 删除按钮

        public LedResourceViewHolder(View itemView) {
            super(itemView);
            // 绑定视图元素
            nameTextView = itemView.findViewById(R.id.Author);
            resourceTextView = itemView.findViewById(R.id.resource_name);
            resourceIv = itemView.findViewById(R.id.iv_img);
            deleteImageView = itemView.findViewById(R.id.deleteLed);
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
                            Glide.with(context)
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
                                Glide.with(context)
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

