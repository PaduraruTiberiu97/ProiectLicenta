package com.tibep.proiectlicenta.ui.home.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.tibep.proiectlicenta.R;
import com.tibep.proiectlicenta.databasefavorite.FavoriteList;
import com.tibep.proiectlicenta.ui.ar.ArActivity;
import com.tibep.proiectlicenta.ui.home.HomeFragment;

import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {

    private ArrayList<HomeItems> homeItems;

    static class HomeViewHolder extends RecyclerView.ViewHolder {

        private ImageView itemImage;
        private TextView itemName;
        private TextView itemPrice;
        private ToggleButton toggleButton;
        private CardView cardView;

        private HomeViewHolder(@NonNull View itemView) {
            super(itemView);

            this.cardView = itemView.findViewById(R.id.cardview_home);
            this.itemImage = itemView.findViewById(R.id.img_itemImage);
            this.itemName = itemView.findViewById(R.id.txt_itemName);
            this.itemPrice = itemView.findViewById(R.id.txt_itemPrice);
            this.toggleButton = itemView.findViewById(R.id.toggle_btn_fav);

        }
    }

    public HomeAdapter(ArrayList<HomeItems> homeItems) {
        this.homeItems = homeItems;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_glasses, parent, false);
        return new HomeViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        final HomeItems currentItem = homeItems.get(position);

        holder.itemImage.setImageResource(currentItem.getItemImgResource());
        holder.itemName.setText(currentItem.getItemName());
        holder.itemPrice.setText((currentItem.getItemPrice() + " RON"));

        if (HomeFragment.favoriteDatabase.favoriteDao().isFavorite(currentItem.getId()) == 1)
            holder.toggleButton.setChecked(true);
        else
            holder.toggleButton.setChecked(false);


        holder.toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FavoriteList favoriteList = new FavoriteList();

                String itemDescription = currentItem.getItemDescription();
                int imageGif = currentItem.getItemGif();
                int image3D = currentItem.getItem3DModel();
                int id = currentItem.getId();
                int image = currentItem.getItemImgResource();
                String name = currentItem.getItemName();
                double price = currentItem.getItemPrice();

                favoriteList.setItemDescription(itemDescription);
                favoriteList.setImageGif(imageGif);
                favoriteList.setImage3D(image3D);
                favoriteList.setId(id);
                favoriteList.setImage(image);
                favoriteList.setName(name);
                favoriteList.setPrice(price);

                if (HomeFragment.favoriteDatabase.favoriteDao().isFavorite(id) != 1) {
                    holder.toggleButton.setChecked(true);
                    HomeFragment.favoriteDatabase.favoriteDao().addData(favoriteList);
                } else {
                    holder.toggleButton.setChecked(false);
                    HomeFragment.favoriteDatabase.favoriteDao().delete(favoriteList);
                }
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int image3D = currentItem.getItem3DModel();
                Intent intent = new Intent(v.getContext(), ArActivity.class);
                intent.putExtra("image3DfromHome", image3D);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return homeItems.size();
    }
}
