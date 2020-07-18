package com.tibep.proiectlicenta.ui.shoppingcart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tibep.proiectlicenta.R;
import com.tibep.proiectlicenta.databaseshoppingcart.ShoppingCartList;
import com.tibep.proiectlicenta.ui.MainActivity;
import com.tibep.proiectlicenta.ui.shoppingcart.onShoppingItemRemovedListener;

import java.util.ArrayList;

public class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingCartAdapter.ShoppingCartViewHolder> {

    private ArrayList<ShoppingCartList> shoppingCartList;
    private onShoppingItemRemovedListener listener;


    static class ShoppingCartViewHolder extends RecyclerView.ViewHolder {

        private ImageView shoppingCartImage;
        private TextView shoppingCartName;
        private TextView shoppingCartPrice;
        private TextView shoppingCartCount;
        private ImageButton shoppingCartDelete;


        private ShoppingCartViewHolder(@NonNull View itemView) {
            super(itemView);
            this.shoppingCartImage = itemView.findViewById(R.id.img_shoppingCart_image);
            this.shoppingCartName = itemView.findViewById(R.id.txt_shoppingCart_name);
            this.shoppingCartPrice = itemView.findViewById(R.id.txt_shoppingCart_price);
            this.shoppingCartDelete = itemView.findViewById(R.id.btn_shoppingCart_delete);
            this.shoppingCartCount = itemView.findViewById(R.id.txt_shoppingCart_itemCount);
        }
    }

    public ShoppingCartAdapter(ArrayList<ShoppingCartList> shoppedItems,onShoppingItemRemovedListener listener) {
        this.shoppingCartList = shoppedItems;
        this.listener=listener;
    }

    @NonNull
    @Override
    public ShoppingCartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_shoppingcart, parent, false);
        return new ShoppingCartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingCartViewHolder holder, int position) {
        ShoppingCartList currentItem = shoppingCartList.get(position);

        holder.shoppingCartName.setText(currentItem.getName());
        holder.shoppingCartPrice.setText((currentItem.getPrice() + " RON"));
        holder.shoppingCartImage.setImageResource(currentItem.getImage());
        holder.shoppingCartCount.setText(String.valueOf(currentItem.getItemCount()));


        holder.shoppingCartDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int holderPosition = holder.getAdapterPosition();
                removeItem(holderPosition, v.getContext());
                MainActivity.shoppingCartDatabase.shoppingCartDao().delete(currentItem);
                listener.calculateRecyclerView();

            }
        });
    }

    private void removeItem(int position, Context context) {
        shoppingCartList.remove(position);

        if (shoppingCartList.isEmpty()) {
            Toast.makeText(context, "Nu mai exista niciun produs in cos", Toast.LENGTH_LONG).show();
            listener.onRecyclerViewEmpty();
        }
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return shoppingCartList.size();
    }

}
