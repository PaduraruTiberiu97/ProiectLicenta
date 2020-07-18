package com.tibep.proiectlicenta.databaseshoppingcart;

import androidx.room.RoomDatabase;

@androidx.room.Database(entities = {ShoppingCartList.class},version = 5)
public abstract class ShoppingCartDatabase extends RoomDatabase {
    public abstract ShoppingCartDao shoppingCartDao();
}
