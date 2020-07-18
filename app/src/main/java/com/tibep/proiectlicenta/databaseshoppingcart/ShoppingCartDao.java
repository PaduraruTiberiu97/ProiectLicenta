package com.tibep.proiectlicenta.databaseshoppingcart;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ShoppingCartDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addData(ShoppingCartList shoppingCartList);

    @Update
    void updateData(ShoppingCartList shoppingCartList);


    @Query("select * from shoppingCartList")
    List<ShoppingCartList> getShoppingCartData();

    @Delete
    void delete(ShoppingCartList shoppingCartList);

    @Query("DELETE FROM shoppingCartList")
    void deleteAll();
}
