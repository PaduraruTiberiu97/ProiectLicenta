package com.tibep.proiectlicenta.ui.saveditems;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tibep.proiectlicenta.R;
import com.tibep.proiectlicenta.databasefavorite.FavoriteList;
import com.tibep.proiectlicenta.ui.home.HomeFragment;
import com.tibep.proiectlicenta.ui.saveditems.adapter.SavedItemsAdapter;

import java.util.ArrayList;
import java.util.List;

public class SavedItemsFragment extends Fragment implements onSavedItemRemovedListener {

    private RecyclerView savedItemsRecyclerView;
    private TextView textView;
    private Button button;
    private NavController navController = null;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_saveditems, container, false);

        bindUI(root);
        buildSavedItemsRecyclerView();
        setOnClickListener();
        return root;
    }

    private void bindUI(View root) {
        savedItemsRecyclerView = root.findViewById(R.id.rv_savedItems);
        textView = root.findViewById(R.id.txt_noItems);
        button = root.findViewById(R.id.btn_noItems);
    }

    private void buildSavedItemsRecyclerView() {

        List<FavoriteList> favoriteList = HomeFragment.favoriteDatabase.favoriteDao().getFavoriteData();
        savedItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        savedItemsRecyclerView.setHasFixedSize(true);
        RecyclerView.Adapter savedItemsAdapter = new SavedItemsAdapter((ArrayList<FavoriteList>) favoriteList, this);
        savedItemsRecyclerView.setAdapter(savedItemsAdapter);

        verifyIfRecyclerViewIsEmpty(savedItemsAdapter);
    }

    private void setOnClickListener() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_nav_saveditems_to_nav_home);
            }
        });
    }

    private void verifyIfRecyclerViewIsEmpty(RecyclerView.Adapter adapter) {
        if (adapter.getItemCount() == 0) {
            savedItemsRecyclerView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);
        } else {
            savedItemsRecyclerView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);
            button.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRecyclerViewEmpty() {
        savedItemsRecyclerView.setVisibility(View.GONE);
        textView.setVisibility(View.VISIBLE);
        button.setVisibility(View.VISIBLE);
    }
}