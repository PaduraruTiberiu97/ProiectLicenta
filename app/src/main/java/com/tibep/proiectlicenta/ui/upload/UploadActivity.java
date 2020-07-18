package com.tibep.proiectlicenta.ui.upload;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.tibep.proiectlicenta.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UploadActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imgChooseImage;
    private Button btnUpload;
    private EditText editTextImageName;
    private ProgressBar progressBar;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private StorageTask uploadTask; //variable to not let the user uplaod multiple times while the upload is still in the making
    private Uri imageUri;
    private String userName;
    private String timeStamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String currentUser = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore.collection("Users").document(currentUser).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        userName = task.getResult().getString("name");
                    }
                }
            }
        });

        storageReference = FirebaseStorage.getInstance().getReference("uploads");  //we will save it in a folder called "uploads" in our storage
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");

        bindUI();
        setClickListeners();
    }

    private void bindUI() {
        imgChooseImage = findViewById(R.id.img_chooseImageToUpload);
        btnUpload = findViewById(R.id.btn_upload);
        editTextImageName = findViewById(R.id.edt_txt_nameImageToUpload);
        progressBar = findViewById(R.id.progress_bar_upload);
    }

    private void setClickListeners() {
        imgChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChosenFile();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(UploadActivity.this, "Incarcare in desfasurare", Toast.LENGTH_SHORT).show();

                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    uploadFile();
                }
            }
        });
    }

    private void openChosenFile() {
        Intent intent = new Intent();
        intent.setType("image/*"); //to only see images
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override //called when we pick a file
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data); //data is the image

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) { //checks for user request, if the user picked an image and if we get something back
            imageUri = data.getData();

            Picasso.with(UploadActivity.this).load(imageUri).into(imgChooseImage);
        }
    }

    private String getFileExtension(Uri uri) { //this method will return the extension of the file we pick
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadFile() {
        if (imageUri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            uploadTask = fileReference.putFile(imageUri);
            fileReference.putFile(imageUri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = 100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount();
                    progressBar.setProgress((int) progress);

                }
            }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {

                            Toast.makeText(UploadActivity.this, "Incarcare nereusita: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();

                        Upload upload = new Upload(editTextImageName.getText().toString().trim(),
                                downloadUri.toString(),getUserName(),getTimeStamp());
                        progressBar.setVisibility(View.VISIBLE);

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setProgress(0);
                                progressBar.setVisibility(View.INVISIBLE);

                            }
                        }, 2000);
                        Toast.makeText(UploadActivity.this, "Incarcare reusita", Toast.LENGTH_LONG).show();
                        databaseReference.push().setValue(upload);

                    } else {
                        Toast.makeText(UploadActivity.this, "Incarcare nereusita: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Nu ati selectat nici o imagine!", Toast.LENGTH_SHORT).show();
        }

    }

    private String getUserName(){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String currentUser = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore.collection("Users").document(currentUser).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        userName = task.getResult().getString("name");
                    }
                }
            }
        });
        return userName;
    }

    private String getTimeStamp(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        timeStamp = simpleDateFormat.format(new Date());
        return timeStamp;
    }

}