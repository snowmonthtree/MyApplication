package com.example.myapplication.ui;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.myapplication.Controller.LedListController;
import com.example.myapplication.Controller.LedResourceController;
import com.example.myapplication.R;
import com.example.myapplication.data.Result.ResultItem;
import com.example.myapplication.data.ViewSharer;
import com.example.myapplication.page.Bluetooth.BluetoothActivity;
import com.example.myapplication.page.Video.PlayVideoActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import pl.droidsonroids.gif.GifDrawable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LedListAdapter extends RecyclerView.Adapter<LedListAdapter.LedListViewHolder> {

    private List<ResultItem> resultList;
    private LedResourceController ledResourceController;
    private Context context;
    private LedListController ledListController;
    private ViewSharer viewSharer;
    private String listId;


    public LedListAdapter(List<ResultItem> resultList,LedResourceController ledResourceController,LedListController ledListController,ViewSharer viewSharer,Context context) {
        this.resultList = resultList;
        this.ledResourceController=ledResourceController;
        this.context=context;
        this.ledListController=ledListController;
        this.viewSharer=viewSharer;

    }

    @NonNull
    @Override
    public LedListAdapter.LedListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_detail, parent, false);
        return new LedListAdapter.LedListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LedListAdapter.LedListViewHolder holder, int position) {
        ResultItem item = resultList.get(position);
        holder.titleTextView.setText(item.getTitle());
        holder.descriptionTextView.setText(item.getDescription());
        holder.resourceId.setText(item.getResourceId());
        holder.textNo.setText(""+(position+1));
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
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("删除资源?")
                        .setMessage("该操作无法撤销")
                        .setPositiveButton("确定",(dialog,which)->{
                            Call<String> call=ledListController.removeResourceFromPlaylist(viewSharer.getUser().getUserId(),listId,item.getResourceId());
                            call.enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    Toast.makeText(context, response.body(), Toast.LENGTH_SHORT).show();
                                    resultList.remove(holder.getAdapterPosition());
                                    notifyDataSetChanged();
                                }


                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        })
                        .setNegativeButton("取消",(dialog,which)->{

                        })
                        .show();
                return true;
            }
        });
    }


    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public void updateData(List<ResultItem> newData,String listId) {
        resultList.clear();
        resultList.addAll(newData);
        this.listId=listId;
        notifyDataSetChanged();
    }

    public static class LedListViewHolder extends RecyclerView.ViewHolder {
        public ImageView iconImageView;
        public TextView titleTextView;
        public TextView descriptionTextView;
        public TextView resourceId;
        public TextView textNo;


        public LedListViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.iconImageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            resourceId = itemView.findViewById(R.id.resourceId);
            textNo = itemView.findViewById(R.id.textNo);
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
                            try {
                                // 加载 GIF
                                GifDrawable gifDrawable = new GifDrawable(imageData);
                                // 设置到 ImageView
                                imageView.setImageDrawable(gifDrawable);
                            } catch (IOException e) {
                                e.printStackTrace();
                                // 错误处理
                                imageView.setImageResource(R.drawable.ic_doodle_back);  // 设置错误图片
                            }
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
    public List<Bitmap> getListToDisplay(RecyclerView recyclerView){
        List<Bitmap> listToDisplay=new ArrayList<>();
        listToDisplay.clear();
        for (int j=0;j<getItemCount();j++) {
            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(j);
            if (viewHolder!=null) {
                ImageView imageView = (viewHolder).itemView.findViewById(R.id.iconImageView);

                // 获取 ImageView 中的 Drawable
                Drawable drawable = imageView.getDrawable();
                if (drawable instanceof BitmapDrawable) {
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    listToDisplay.add(bitmap);
                    // 在这里处理 Bitmap
                } else if ((drawable instanceof GifDrawable)) {
                    GifDrawable gifDrawable = (GifDrawable) drawable;
                    int frameCount = gifDrawable.getNumberOfFrames();

                    // 创建一个 List 来保存缩放后的帧

                    // 遍历所有帧
                    for (int i = 0; i < frameCount; i++) {
                        // 获取第 i 帧
                        Bitmap frame = gifDrawable.seekToFrameAndGet(i);
                        // 将缩放后的帧添加到列表中
                        listToDisplay.add(frame);
                    }
                }
                System.out.println(listToDisplay);
            }
        }
        return listToDisplay;
    }

}
