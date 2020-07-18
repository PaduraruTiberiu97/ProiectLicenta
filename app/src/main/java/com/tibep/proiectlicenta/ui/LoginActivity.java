package com.tibep.proiectlicenta.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.tibep.proiectlicenta.R;
import com.tibep.proiectlicenta.databasefavorite.FavoriteDatabase;
import com.tibep.proiectlicenta.databaseshoppingcart.ShoppingCartDatabase;

public class LoginActivity extends AppCompatActivity {
    EditText emailId, password;
    Button btnSignIn;
    TextView tvSignUp;
    FirebaseAuth mFirebaseAuth;
    private ProgressBar progressBar;
    String newUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.editEL);
        password = findViewById(R.id.editPL);
        btnSignIn = findViewById(R.id.buttonSignIp);
        progressBar = findViewById(R.id.progress_bar_login);
        tvSignUp = findViewById(R.id.textViewSignUpHere);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailId.getText().toString();
                String pwd = password.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    emailId.setError("Introduceti o adresa de email");
                    emailId.requestFocus();

                } else if (TextUtils.isEmpty(pwd)) {
                    password.setError("Introduceti o parola");
                    password.requestFocus();

                } else if (TextUtils.isEmpty(email) && TextUtils.isEmpty(pwd)) {
                    Toast.makeText(LoginActivity.this, "Au ramas campuri necompletate", Toast.LENGTH_SHORT).show();

                } else if (!(TextUtils.isEmpty(email) && TextUtils.isEmpty(pwd))) {
                    progressBar.setVisibility(View.VISIBLE);

                    mFirebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {


                                progressBar.setVisibility(View.INVISIBLE);
                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, "Eroare: " + errorMessage, Toast.LENGTH_SHORT).show();

                            } else {

                                newUser = mFirebaseAuth.getCurrentUser().getUid();
                                checkUser(newUser);
                                sendToMain();
                            }
                        }
                    });
                } else {
                    Toast.makeText(LoginActivity.this, "Eroare", Toast.LENGTH_SHORT).show();

                }

            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intSignUp = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intSignUp);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            sendToMain();

        }
    }

    private void sendToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

    }


    public void onBackPressed() {
        finishAffinity();
    }


    private void checkUser(String newUser) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String lastUser;
        lastUser = sharedPreferences.getString("user", null);

        if (!newUser.equals(lastUser)) {
            Toast.makeText(LoginActivity.this, "Utilizator nou", Toast.LENGTH_SHORT).show();

            FavoriteDatabase favoriteDatabase;
            ShoppingCartDatabase shoppingCartDatabase;
            favoriteDatabase = Room.databaseBuilder(LoginActivity.this, FavoriteDatabase.class, "myfavdb")
                    .allowMainThreadQueries().fallbackToDestructiveMigration().build();
            shoppingCartDatabase = Room.databaseBuilder(LoginActivity.this, ShoppingCartDatabase.class, "myshopdb")
                    .allowMainThreadQueries().fallbackToDestructiveMigration().build();

            shoppingCartDatabase.shoppingCartDao().deleteAll();
            favoriteDatabase.favoriteDao().deleteAll();
            editor.clear();
            editor.putString("user", newUser);
            editor.apply();
        }
    }
}

