package com.tibep.proiectlicenta.ui.upload.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.tibep.proiectlicenta.R;
import com.tibep.proiectlicenta.ui.upload.Upload;

import java.util.List;

public class UploadAdapter extends RecyclerView.Adapter<UploadAdapter.UploadViewHolder> {
    private Context context;
    private List<Upload> mUploads;

    public UploadAdapter(Context context, List<Upload> uploads) {
        this.context = context;
        mUploads = uploads;
    }

    @NonNull
    @Override
    public UploadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_upload, parent, false);
        return new UploadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UploadViewHolder holder, int position) {
        Upload currentUpload = mUploads.get(position);
        holder.textView.setText(currentUpload.getDescription());
        Picasso.with(context).load(currentUpload.getImageURL()).fit().centerCrop().into(holder.imageView);
        holder.timeStamp.setText(currentUpload.getTimestamp());
        holder.user.setText(currentUpload.getName());

    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    static class UploadViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;
        TextView timeStamp;
        TextView user;

        private UploadViewHolder(@NonNull View itemView) {
            super(itemView);
            timeStamp=itemView.findViewById(R.id.txt_timeStamp);
            user=itemView.findViewById(R.id.txt_userWhoUploaded);
            textView = itemView.findViewById(R.id.txt_uploaded_image_text);
            imageView = itemView.findViewById(R.id.img_rv_uploadedImg);
        }
    }

}

