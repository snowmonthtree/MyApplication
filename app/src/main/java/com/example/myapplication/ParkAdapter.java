package com.example.myapplication;


import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.myapplication.Controller.LedResourceController;
import com.example.myapplication.Controller.UserController;
import com.example.myapplication.data.LedResource.LedResource;
import com.example.myapplication.page.Video.PlayVideoActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// 适配器
public class ParkAdapter extends RecyclerView.Adapter<ParkAdapter.ParkViewHolder> {

    private List<LedResource> list;  // 数据源
    private LedResourceController ledResourceController;  // 控制器，用于与服务器或数据库交互
    private UserController userController;  // 用户控制器
    private Context context;  // 上下文，用于显示UI元素

    // 构造函数
    public ParkAdapter(Context context, List<LedResource> list, LedResourceController ledResourceController, UserController userController) {
        this.context = context;
        this.list = list;
        this.ledResourceController = ledResourceController;
        this.userController = userController;
    }

    @Override
    public ParkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate item view
        View itemView = LayoutInflater.from(context).inflate(R.layout.park_item, parent, false);
        return new ParkViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ParkViewHolder holder, int position) {
        // 获取当前的资源对象
        LedResource resource = list.get(position);

        // 绑定数据到视图
        holder.ledNameTextView.setText(resource.getName());
        holder.AuthorTexView.setText(resource.getUser().getName());
        holder.resourceCreateTimeTextView.setText(resource.getUpTime().toString().substring(0,16));
        fetchImage(resource.getViewWebUrl(),holder.ledImageView);
        userController.getAvatar(resource.getUser().getUserId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 从ResponseBody获取图片数据并设置到ImageButton
                    ResponseBody responseBody = response.body();
                    byte[] imageData = convertResponseBodyToBytes(responseBody);

                    if (imageData != null ) {
                        Glide.with(context)
                                .load(imageData)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .placeholder(R.drawable.ic_caution_refresh)
                                .error(R.drawable.ic_doodle_back)
                                .into(holder.userImageView);
                                // 设置ImageButton

                    }
                } else {
                    // 图片加载失败，处理错误
                    Log.e("1", "onResponse: " );
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // 网络请求失败
                Log.e("2", "onFailure: ");
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PlayVideoActivity.class);
                intent.putExtra("ledId",resource.getResourceId());
                context.startActivity(intent);
            }
        });



        // 处理点击事件或其他交互


    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    // 更新数据的方法
    public void updateData(List<LedResource> newList) {
        this.list = newList;
        notifyDataSetChanged();  // 通知适配器数据更新
    }

    // ViewHolder类
    public static class ParkViewHolder extends RecyclerView.ViewHolder {

        TextView ledNameTextView;
        TextView AuthorTexView;
        TextView resourceCreateTimeTextView;
        ImageView ledImageView;
        ImageView userImageView;

        public ParkViewHolder(View itemView) {
            super(itemView);
            ledNameTextView = itemView.findViewById(R.id.ledname);
            AuthorTexView = itemView.findViewById(R.id.author);
            resourceCreateTimeTextView=itemView.findViewById(R.id.create_time1);
            ledImageView=itemView.findViewById(R.id.image_ic);
            userImageView=itemView.findViewById(R.id.image_ic_user);
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

