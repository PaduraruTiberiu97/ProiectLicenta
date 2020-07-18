package com.tibep.proiectlicenta.ui.saveditems.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.tibep.proiectlicenta.R;
import com.tibep.proiectlicenta.databasefavorite.FavoriteList;
import com.tibep.proiectlicenta.ui.ar.ArActivity;
import com.tibep.proiectlicenta.ui.home.HomeFragment;
import com.tibep.proiectlicenta.ui.saveditems.PopUp;
import com.tibep.proiectlicenta.ui.saveditems.onSavedItemRemovedListener;

import java.util.ArrayList;

public class SavedItemsAdapter extends RecyclerView.Adapter<SavedItemsAdapter.SavedItemsViewHolder> {

    private ArrayList<FavoriteList> favoriteList;
    private onSavedItemRemovedListener listener;


    static class SavedItemsViewHolder extends RecyclerView.ViewHolder {

        private ImageView savedItemImage;
        private TextView savedItemName;
        private TextView savedItemPrice;
        private ImageButton deleteButton;
        private ImageButton tryOnButton;
        private CardView cardView;

        private SavedItemsViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tryOnButton = itemView.findViewById(R.id.btn_savedItem_try);
            this.savedItemImage = itemView.findViewById(R.id.img_savedItem_image);
            this.savedItemName = itemView.findViewById(R.id.txt_savedItem_name);
            this.savedItemPrice = itemView.findViewById(R.id.txt_savedItem_price);
            this.deleteButton = itemView.findViewById(R.id.btn_savedItem_delete);
            this.cardView = itemView.findViewById(R.id.cardview_savedItems);


        }
    }

    public SavedItemsAdapter(ArrayList<FavoriteList> favoriteList,onSavedItemRemovedListener listener) {
        this.favoriteList = favoriteList;
        this.listener=listener;
    }

    @NonNull
    @Override
    public SavedItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_saveditems, parent, false);
        return new SavedItemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedItemsViewHolder holder, int position) {
        FavoriteList currentItem = favoriteList.get(position);

        holder.savedItemName.setText(currentItem.getName());
        holder.savedItemPrice.setText((currentItem.getPrice() + " RON"));
        holder.savedItemImage.setImageResource(currentItem.getImage());

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int holderPosition = holder.getAdapterPosition();
                removeItem(holderPosition, v.getContext());
                HomeFragment.favoriteDatabase.favoriteDao().delete(currentItem);

            }
        });

        holder.tryOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int image3D = currentItem.getImage3D();
                Intent intent = new Intent(v.getContext(), ArActivity.class);
                intent.putExtra("image3DfromSaved", image3D);
                v.getContext().startActivity(intent);
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemId = currentItem.getId();
                int itemImage = currentItem.getImage();
                int imageGif = currentItem.getImageGif();
                String itemName = currentItem.getName();
                double itemPrice = currentItem.getPrice();
                String description = currentItem.getItemDescription();
                Intent intent = (new Intent(v.getContext(), PopUp.class));
                intent.putExtra("itemName", itemName);
                intent.putExtra("itemPrice", itemPrice);
                intent.putExtra("description", description);
                intent.putExtra("imageGif", imageGif);
                intent.putExtra("itemImage", itemImage);
                intent.putExtra("itemIdd", itemId);
                v.getContext().startActivity(intent);

            }
        });

    }

    private void removeItem(int position, Context context) {
        favoriteList.remove(position);
        if (favoriteList.isEmpty()) {
            Toast.makeText(context, "Nu mai exista niciun produs salvat", Toast.LENGTH_LONG).show();
            listener.onRecyclerViewEmpty();
        }
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

}
