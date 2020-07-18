package com.tibep.proiectlicenta.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.FirebaseFirestore;
import com.tibep.proiectlicenta.R;
import com.tibep.proiectlicenta.databasefavorite.FavoriteDatabase;
import com.tibep.proiectlicenta.databaseshoppingcart.ShoppingCartDatabase;

import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity {
    EditText edtEmailId, edtPassword, edtPasswordVer, edtUserName;
    Button btnSignUp;
    TextView tvSignIn;
    FirebaseAuth firebaseAuth;
    private String user_id;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        edtEmailId = findViewById(R.id.editER);
        edtPassword = findViewById(R.id.editPR);
        edtPasswordVer = findViewById(R.id.editPRV);
        edtUserName = findViewById(R.id.editUserName);
        btnSignUp = findViewById(R.id.buttonSignUp);
        tvSignIn = findViewById(R.id.textViewSignInHere);

        firebaseFirestore = FirebaseFirestore.getInstance();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmailId.getText().toString();
                String password = edtPassword.getText().toString();
                String passwordVer = edtPasswordVer.getText().toString();
                String userName = edtUserName.getText().toString();

                if (TextUtils.isEmpty(userName)) {
                    edtUserName.setError("Introduceti un nume de utilizator");
                    edtUserName.requestFocus();

                } else if (TextUtils.isEmpty(email)) {
                    edtEmailId.setError("Introduceti o adresa de email");
                    edtEmailId.requestFocus();

                } else if (TextUtils.isEmpty(password)) {
                    edtPassword.setError("Introduceti o parola");
                    edtPassword.requestFocus();

                } else if (!(password.equals(passwordVer))) {
                    edtPasswordVer.setError("Parolele nu coincid");
                    edtPasswordVer.requestFocus();

                } else if (!(TextUtils.isEmpty(email) && TextUtils.isEmpty(password))) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {

                                String errorMessage = task.getException().toString();
                                Toast.makeText(RegisterActivity.this, "Eroare: " + errorMessage, Toast.LENGTH_SHORT).show();

                            } else {

                                user_id = firebaseAuth.getCurrentUser().getUid();

                                checkUser(user_id);

                                storeFirestore(userName);

                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        }
                    });
                } else {
                    Toast.makeText(RegisterActivity.this, "Eroare", Toast.LENGTH_SHORT).show();

                }
            }
        });

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void storeFirestore(String user_name) {

        Map<String, String> userMap = new HashMap<>();
        userMap.put("name", user_name);

        firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (!task.isSuccessful()) {
                    String errorMessage = task.getException().toString();
                    Toast.makeText(RegisterActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void checkUser(String newUser) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String lastUser;
        lastUser = sharedPreferences.getString("user", null);

        if (!newUser.equals(lastUser)) {
            Toast.makeText(RegisterActivity.this, "Utilizator nou", Toast.LENGTH_SHORT).show();

            FavoriteDatabase favoriteDatabase;
            ShoppingCartDatabase shoppingCartDatabase;
            favoriteDatabase = Room.databaseBuilder(RegisterActivity.this, FavoriteDatabase.class, "myfavdb").allowMainThreadQueries().fallbackToDestructiveMigration().build();           //fallback-if we update the database we also need to provide a migration to the new database so all the data will remain saved.what fallback does is just deleting everythign from the database so we dont need to make a migration
            shoppingCartDatabase = Room.databaseBuilder(RegisterActivity.this, ShoppingCartDatabase.class, "myshopdb").allowMainThreadQueries().fallbackToDestructiveMigration().build();

            shoppingCartDatabase.shoppingCartDao().deleteAll();
            favoriteDatabase.favoriteDao().deleteAll();
            editor.clear();
            editor.putString("user", newUser);
            editor.apply();
        }
    }

}
