package com.example.myapplication.ui;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.myapplication.R;
import com.example.myapplication.page.Bluetooth.BluetoothActivity;
import com.example.myapplication.page.CreationCenter.CreationCenterActivity;
import com.example.myapplication.page.CreationCenter.UpLoad.upLoadActivity;

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
        holder.imageView.setImageURI(item);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if (context instanceof CreationCenterActivity) {
                    intent = new Intent(context, upLoadActivity.class);
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