package com.example.myapplication.ui;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.myapplication.R;
import com.example.myapplication.page.Bluetooth.BluetoothActivity;
import com.example.myapplication.page.CreationCenter.CreationCenterActivity;
import com.example.myapplication.page.CreationCenter.UpLoad.UpLoadActivity;
import com.example.myapplication.page.Video.PlayVideoActivity;

import java.io.File;
import java.util.List;

public class LocalAdapter extends RecyclerView.Adapter<LocalAdapter.LocalViewHolder> {

    private List<Uri> localList;
    private Context context;

    public LocalAdapter(List<Uri> localList,Context context) {
        this.localList = localList;
        this.context=context;
    }

    @NonNull
    @Override
    public LocalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.upload_item, parent, false);
        return new LocalViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LocalViewHolder holder, int position) {
        Uri item = localList.get(position);
        File gifFile = new File(item.getPath());
        Glide.with(context)
                .load(gifFile)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_caution_refresh)
                .error(R.drawable.ic_doodle_back)
                .into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if (context instanceof CreationCenterActivity) {
                    intent = new Intent(context, UpLoadActivity.class);
                }
                else {
                    intent=new Intent(context, BluetoothActivity.class);
                }
                intent.putExtra("uri",item);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return localList.size();
    }

    static class LocalViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        LocalViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.localImageView);
        }
    }
}