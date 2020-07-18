package com.tibep.proiectlicenta.ui.home.adapter;

public class HomeItems {

    private int id;
    private String itemName;
    private double itemPrice;
    private int itemImgResource;
    private int item3DModel;
    private int itemGif;
    private String itemDescription;


    public HomeItems(String itemName, double itemPrice, int itemImgResource, int id, int item3DModel, int itemGif,String itemDescription) {
        this.itemDescription=itemDescription;
        this.itemGif = itemGif;
        this.item3DModel = item3DModel;
        this.id = id;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemImgResource = itemImgResource;

    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }


    public int getItemGif() {
        return itemGif;
    }

    public void setItemGif(int itemGif) {
        this.itemGif = itemGif;
    }

    public int getItem3DModel() {
        return item3DModel;
    }

    public void setItem3DModel(int item3DModel) {
        this.item3DModel = item3DModel;
    }

    public int getItemImgResource() {
        return itemImgResource;
    }

    public void setItemImgResource(int itemImgResource) {
        this.itemImgResource = itemImgResource;
    }

    String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
