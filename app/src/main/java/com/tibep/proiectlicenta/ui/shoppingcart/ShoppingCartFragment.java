package com.tibep.proiectlicenta.ui.shoppingcart;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.tibep.proiectlicenta.R;
import com.tibep.proiectlicenta.databaseshoppingcart.ShoppingCartList;
import com.tibep.proiectlicenta.ui.MainActivity;
import com.tibep.proiectlicenta.ui.ar.ArActivity;
import com.tibep.proiectlicenta.ui.shoppingcart.adapter.ShoppingCartAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ShoppingCartFragment extends Fragment implements onShoppingItemRemovedListener {

    private RecyclerView recyclerView;
    private TextView textViewTotal;
    private TextView textView;
    private Button btnBuy;
    private Button btnAdd;
    private ConstraintLayout constraintLayout;
    private RecyclerView.Adapter shopAdapter;
    private NavController navController = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_shoppingcart, container, false);

        bindUI(root);
        buildRecyclerView();
        verifyIfRecyclerViewIsEmpty(shopAdapter, recyclerView);

        return root;
    }

    private void bindUI(View root) {
        recyclerView = root.findViewById(R.id.rv_shoppingCart);
        textViewTotal = root.findViewById(R.id.txt_shoppingCart_total);
        btnBuy = root.findViewById(R.id.btn_shoppingCart_buy);
        constraintLayout = root.findViewById(R.id.layout_buy);
        textView = root.findViewById(R.id.txt_shoppingCart_noItems);
        btnAdd = root.findViewById(R.id.btn_shoppingCart_addItems);

    }

    private void buildRecyclerView() {

        List<ShoppingCartList> shoppingCartList = MainActivity.shoppingCartDatabase.shoppingCartDao().getShoppingCartData();
        calculateTotal(shoppingCartList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        shopAdapter = new ShoppingCartAdapter((ArrayList<ShoppingCartList>) shoppingCartList,this);
        recyclerView.setAdapter(shopAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                MainActivity.shoppingCartDatabase.shoppingCartDao().delete(shoppingCartList.get(position));
                shoppingCartList.remove(position);
                shopAdapter.notifyDataSetChanged();
                verifyIfRecyclerViewIsEmpty(shopAdapter, recyclerView);
                calculateTotal(shoppingCartList);

            }
        }).attachToRecyclerView(recyclerView);

        setClickListener(shoppingCartList,recyclerView);

    }

    private void calculateTotal(List<ShoppingCartList> list) {
        double total = 0;
        for (int i = 0; i <= list.size() - 1; i++) {
            total = total + list.get(i).getPrice() * list.get(i).getItemCount();
            textViewTotal.setText(total + " Ron");
        }
    }

    private void setClickListener(List<ShoppingCartList> shoppingCartList,RecyclerView recyclerView) {
        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                builder.setMessage("Finalizati comanda?");
                builder.setPositiveButton("DA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        FirebaseFirestore.getInstance();
                        for (int i = 0; i <= shoppingCartList.size() - 1; i++) {
                            FirebaseFirestore firebaseFirestore;
                            firebaseFirestore = FirebaseFirestore.getInstance();

                            Map<String, List<ShoppingCartList>> userMap = new HashMap<>();
                            userMap.put("items", shoppingCartList);
                            firebaseFirestore.collection("Orders").document(shoppingCartList.get(i).getCurrentUserId()).set(userMap);

                        }

                        Toast.makeText(getActivity(), "Comanda a fost trimisa", Toast.LENGTH_SHORT).show();

                        MainActivity.shoppingCartDatabase.shoppingCartDao().deleteAll();
                        shoppingCartList.clear();
                        verifyIfRecyclerViewIsEmpty(shopAdapter, recyclerView);
                        recyclerView.setAdapter(null);
                        shopAdapter.notifyDataSetChanged();

                    }
                });
                builder.setNegativeButton("NU", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog=builder.create();
                dialog.show();

            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_nav_shoppingcart_to_nav_saveditems);
            }
        });

    }

    private void verifyIfRecyclerViewIsEmpty(RecyclerView.Adapter adapter, RecyclerView recyclerView) {
        if (adapter.getItemCount() == 0) {
            constraintLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
            btnAdd.setVisibility(View.VISIBLE);
        } else {
            constraintLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);
            btnAdd.setVisibility(View.GONE);
        }
    }







    @Override
    public void onRecyclerViewEmpty() {

        constraintLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        textView.setVisibility(View.VISIBLE);
        btnAdd.setVisibility(View.VISIBLE);
    }

    @Override
    public void calculateRecyclerView() {
        List<ShoppingCartList> shoppingCartList = MainActivity.shoppingCartDatabase.shoppingCartDao().getShoppingCartData();
        calculateTotal(shoppingCartList);

    }
}