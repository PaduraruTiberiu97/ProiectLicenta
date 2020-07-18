package com.tibep.proiectlicenta.databaseshoppingcart;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "shoppingCartList")
public class ShoppingCartList {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "itemCount")
    private int itemCount;

    @ColumnInfo(name = "currentUserId")
    private String currentUserId;

    @ColumnInfo(name = "image")
    private int image;

    @ColumnInfo(name = "prname")
    private String name;

    @ColumnInfo(name = "price")
    private double price;

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String   getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
