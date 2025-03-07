package com.example.myapplication.ui;


import static androidx.core.content.ContextCompat.startActivity;

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
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Controller.LedResourceController;
import com.example.myapplication.R;
import com.example.myapplication.data.LedResource.LedResource;
import com.example.myapplication.data.Result.ResultItem;
import com.example.myapplication.page.Video.PlayVideoActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultViewHolder> {

    private List<ResultItem> resultList;
    private LedResourceController ledResourceController;
    private Context context;

    public ResultAdapter(List<ResultItem> resultList,LedResourceController ledResourceController,Context context) {
        this.resultList = resultList;
        this.ledResourceController=ledResourceController;
        this.context=context;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result, parent, false);
        return new ResultViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        ResultItem item = resultList.get(position);
        holder.titleTextView.setText(item.getTitle());
        holder.descriptionTextView.setText(item.getDescription());
        holder.resourceId.setText(item.getResourceId());
        String imageName = item.getIconResource();  // 获取图片名称
        // 使用 Glide 和异步加载图片
        fetchImage(imageName, holder.iconImageView);  // 异步加载图片到 ImageView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, PlayVideoActivity.class);
                intent.putExtra("ledId",item.getResourceId());
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public void updateData(List<ResultItem> newData) {
        resultList.clear();
        resultList.addAll(newData);
        notifyDataSetChanged();
    }

    public static class ResultViewHolder extends RecyclerView.ViewHolder {
        public ImageView iconImageView;
        public TextView titleTextView;
        public TextView descriptionTextView;
        public TextView resourceId;

        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.iconImageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            resourceId = itemView.findViewById(R.id.resourceId);
        }
    }
    private void fetchImage(String imageName, ImageView imageView) {
        // 假设 ledResourceController 是 Retrofit 接口，调用 getImage 方法来获取图片
        ledResourceController.getImage(imageName).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseBody responseBody = response.body();
                    Bitmap bitmap = convertResponseBodyToBitmap(responseBody);

                    if (bitmap != null) {
                        // 使用 Glide 将 Bitmap 加载到 ImageView
                        Glide.with(imageView.getContext())
                                .load(bitmap)  // 直接加载 Bitmap
                                .placeholder(R.drawable.test)  // 占位图
                                .error(R.drawable.ic_doodle_back)  // 错误图
                                .into(imageView);  // 将图片显示到 ImageView
                    } else {
                        Log.e("Image Fetch", "Bitmap is null");
                    }
                } else {
                    Log.e("Image Fetch", "onResponse: Image fetch failed1");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // 网络请求失败
                Log.e("Image Fetch", "onFailure: Image fetch failed", t);
            }
        });
    }

    // 将ResponseBody转换为Bitmap
    private Bitmap convertResponseBodyToBitmap(ResponseBody responseBody) {
        Bitmap bitmap = null;
        InputStream inputStream = responseBody.byteStream();
        try {
            bitmap = BitmapFactory.decodeStream(inputStream);  // 将输入流解码为Bitmap
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();  // 关闭输入流
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }
}
