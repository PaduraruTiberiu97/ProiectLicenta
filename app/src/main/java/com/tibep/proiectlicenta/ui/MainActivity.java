package com.tibep.proiectlicenta.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tibep.proiectlicenta.R;
import com.tibep.proiectlicenta.databasefavorite.FavoriteDatabase;
import com.tibep.proiectlicenta.databaseshoppingcart.ShoppingCartDatabase;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;

import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private TextView textView;
    public static ShoppingCartDatabase shoppingCartDatabase;
    public static FavoriteDatabase favoriteDatabase;
    FirebaseAuth mFirebaseAuth;
    String newUser;



    @Override
    protected void onStart() {
        super.onStart();

        verifyIfIsLoggedIn();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        verifyIfIsLoggedIn();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_shoppingcart, R.id.nav_saveditems,
                R.id.nav_account, R.id.nav_upload)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        View headerView = navigationView.getHeaderView(0);
        textView = headerView.findViewById(R.id.txt_heyUser);

        shoppingCartDatabase = Room.databaseBuilder(MainActivity.this, ShoppingCartDatabase.class, "myshopdb").allowMainThreadQueries().fallbackToDestructiveMigration().build();
        favoriteDatabase = Room.databaseBuilder(MainActivity.this, FavoriteDatabase.class, "myfavdb").allowMainThreadQueries().fallbackToDestructiveMigration().build(); //fallback-if we update the database we also need to provide a migration to the new database so all the data will remain saved.what fallback does is just deleting everythign from the database so we dont need to make a migration

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String lastUser;
        lastUser = sharedPreferences.getString("user", null);
        System.out.println(lastUser);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
       public boolean onOptionsItemSelected(MenuItem item){

        if(item.getItemId()==R.id.action_logout){
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void verifyIfIsLoggedIn() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            String userId = currentUser.getUid();
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseFirestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {

                            String name = task.getResult().getString("name");
                            textView.setText("Salut, " + name + "!");
                        }
                    }
                }
            });
        }
    }
    private void checkUser(String newUser) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String lastUser;
        lastUser = sharedPreferences.getString("user", null);

        if (!newUser.equals(lastUser)) {
            Toast.makeText(MainActivity.this, "Utilizator nou", Toast.LENGTH_SHORT).show();

            FavoriteDatabase favoriteDatabase;
            ShoppingCartDatabase shoppingCartDatabase;
            favoriteDatabase = Room.databaseBuilder(MainActivity.this, FavoriteDatabase.class, "myfavdb").allowMainThreadQueries().fallbackToDestructiveMigration().build(); //fallback-if we update the database we also need to provide a migration to the new database so all the data will remain saved.what fallback does is just deleting everythign from the database so we dont need to make a migration
            shoppingCartDatabase = Room.databaseBuilder(MainActivity.this, ShoppingCartDatabase.class, "myshopdb").allowMainThreadQueries().fallbackToDestructiveMigration().build();

            shoppingCartDatabase.shoppingCartDao().deleteAll();
            favoriteDatabase.favoriteDao().deleteAll();
            editor.clear();
            editor.putString("user", newUser);
            editor.apply();
        }
    }

}
