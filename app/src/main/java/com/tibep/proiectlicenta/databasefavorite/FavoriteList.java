package com.tibep.proiectlicenta.databasefavorite;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favoritelist")
public class FavoriteList {

    @PrimaryKey
    private int id;

    @ColumnInfo(name = "itemDescription")
    private String itemDescription;


    @ColumnInfo(name = "imageGif")
    private int imageGif;

    @ColumnInfo(name = "image3D")
    private int image3D;

    @ColumnInfo(name = "image")
    private int image;

    @ColumnInfo(name = "prname")
    private String name;

    @ColumnInfo(name = "price")
    private double price;

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }


    public int getImageGif() {
        return imageGif;
    }

    public void setImageGif(int imageGif) {
        this.imageGif = imageGif;
    }


    public int getImage3D() {
        return image3D;
    }

    public void setImage3D(int image3D) {
        this.image3D = image3D;
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

    public String getName() {
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
