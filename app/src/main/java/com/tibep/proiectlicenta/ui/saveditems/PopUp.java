package com.tibep.proiectlicenta.ui.saveditems;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.tibep.proiectlicenta.R;
import com.tibep.proiectlicenta.databaseshoppingcart.ShoppingCartList;
import com.tibep.proiectlicenta.ui.MainActivity;

public class PopUp extends Activity {

    ShoppingCartList shoppingCartList = new ShoppingCartList();


    EditText editTextItemCount;
    ImageButton btnShop;
    ImageView imageView;
    TextView txtItemDescription;
    TextView txtItemName;
    TextView txtItemPrice;
    FirebaseAuth firebaseAuth;

    int itemId;
    int itemImage;
    int imageGif;
    String description;
    String itemName;
    double itemPrice;

    private String user_id;
    private int itemCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.popup_window);
        getDataFromAdapter();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int heigth = displayMetrics.heightPixels;

        bindUI();

        getWindow().setLayout((int) (width * .8), (int) (heigth * .6));

        Glide.with(this).load(imageGif).into(imageView);
        txtItemDescription.setText(description);
        txtItemName.setText(itemName);
        txtItemPrice.setText(itemPrice + " RON");
        user_id = firebaseAuth.getCurrentUser().getUid();
        onClickListener();

    }

    public void getDataFromAdapter() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            itemId = extras.getInt("itemIdd");
            itemImage = extras.getInt("itemImage");
            itemName = extras.getString("itemName");
            itemPrice = extras.getDouble("itemPrice");
            imageGif = extras.getInt("imageGif");
            description = extras.getString("description");
        }
    }

    private void bindUI() {
        btnShop = findViewById(R.id.popup_addToCart);
        editTextItemCount = findViewById(R.id.edt_txt_shoppedItemCount);
        txtItemName = findViewById(R.id.popup_name);
        txtItemPrice = findViewById(R.id.popup_price);
        imageView = findViewById(R.id.popup_img_gif);
        txtItemDescription = findViewById(R.id.popup_description);
        firebaseAuth = FirebaseAuth.getInstance();

    }

    private void onClickListener() {

        btnShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String verify = editTextItemCount.getText().toString();

                if (TextUtils.isEmpty(verify)) {
                    editTextItemCount.requestFocus();
                } else {
                    itemCount = Integer.parseInt(editTextItemCount.getText().toString());
                }

                if (itemCount <= 0) {
                    Toast.makeText(PopUp.this, "Numarul de produse adaugate nu poate fi 0", Toast.LENGTH_SHORT).show();
                } else if (itemCount > 10) {
                    Toast.makeText(PopUp.this, "Numarul de produse nu poate fi mai mare decat 10", Toast.LENGTH_SHORT).show();
                } else {
                    shoppingCartList.setId(itemId);
                    shoppingCartList.setImage(itemImage);
                    shoppingCartList.setName(itemName);
                    shoppingCartList.setPrice(itemPrice);
                    shoppingCartList.setCurrentUserId(user_id);
                    shoppingCartList.setItemCount(itemCount);

                    MainActivity.shoppingCartDatabase.shoppingCartDao().addData(shoppingCartList);
                    MainActivity.shoppingCartDatabase.shoppingCartDao().updateData(shoppingCartList);

                    finish();

                    Toast.makeText(PopUp.this, "Produsele au fost adaugate in cosul de cumparaturi", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

}
