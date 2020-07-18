package com.tibep.proiectlicenta.ui.upload;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tibep.proiectlicenta.R;
import com.tibep.proiectlicenta.ui.upload.adapter.UploadAdapter;

import java.util.ArrayList;
import java.util.List;

public class UploadFragment extends Fragment {

    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;
    private UploadAdapter uploadAdapter;
    private TextView textView;
    private TextView textViewUploadedImages;
    private ImageView imageView;
    private List<Upload> uploadsArrayList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_upload, container, false);
        bindUI(root);
        setClickListeners();
        buildRV(root);


        return root;
    }

    private void bindUI(View root) {
        textViewUploadedImages=root.findViewById(R.id.textViewUploadedImages);
        floatingActionButton = root.findViewById(R.id.fab);
        textView = root.findViewById(R.id.txt_uploadrvempty);
        imageView=root.findViewById(R.id.img_sad_face_emoji);
        // recyclerView = root.findViewById(R.id.rv_upload);
    }

    private void setClickListeners() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UploadActivity.class);
                startActivity(intent);
            }
        });
    }

    private void buildRV(View view) {

        recyclerView = view.findViewById(R.id.rv_upload);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("uploads");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                uploadsArrayList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    uploadsArrayList.add(upload);
                }
                uploadAdapter = new UploadAdapter(getContext(), uploadsArrayList);
                recyclerView.setAdapter(uploadAdapter);
                checkIfRvEmpty();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
    private void checkIfRvEmpty(){
        if (uploadAdapter.getItemCount() == 0) {
            textViewUploadedImages.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
        } else {
            textViewUploadedImages.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
        }

    }
}
