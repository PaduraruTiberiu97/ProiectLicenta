package com.tibep.proiectlicenta.databasefavorite;

import androidx.room.RoomDatabase;

@androidx.room.Database(entities = {FavoriteList.class},version = 5)
public abstract class FavoriteDatabase extends RoomDatabase {
    public abstract FavoriteDao favoriteDao();
}
