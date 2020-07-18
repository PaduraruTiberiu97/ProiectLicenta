package com.tibep.proiectlicenta.ui.ar;


import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.PixelCopy;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.snackbar.Snackbar;
import com.google.ar.core.AugmentedFace;
import com.google.ar.core.Frame;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.AugmentedFaceNode;
import com.tibep.proiectlicenta.R;
import com.tibep.proiectlicenta.ui.MainActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

public class ArActivity extends AppCompatActivity {

    private CustomArFragment customArFragment;
    int image3DfromHome;
    int image3DfromSaved;
    private ModelRenderable modelRenderable;
    private boolean isAdded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);

        getPositionFromAdapter();

        customArFragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);

        if (image3DfromHome != 0) {
            ModelRenderable
                    .builder().setSource(this, image3DfromHome)
                    .build()
                    .thenAccept(renderable -> {
                        modelRenderable = renderable;
                        modelRenderable.setShadowCaster(false);
                        modelRenderable.setShadowReceiver(false);
                    });
        } else if (image3DfromSaved != 0) {
            ModelRenderable
                    .builder().setSource(this, image3DfromSaved)
                    .build()
                    .thenAccept(renderable -> {
                        modelRenderable = renderable;
                        modelRenderable.setShadowCaster(false);
                        modelRenderable.setShadowReceiver(false);
                    });

        }

        customArFragment.getArSceneView().setCameraStreamRenderPriority(Renderable.RENDER_PRIORITY_FIRST);
        customArFragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {
            if (modelRenderable == null)
                return;
            //The AugmentedFace class extends the Trackable class. In app's activity,  AugmentedFace is used to get access to the detected face
            Frame frame = customArFragment.getArSceneView().getArFrame();
            Collection<AugmentedFace> augmentedFacesList = frame.getUpdatedTrackables(AugmentedFace.class);

            for (AugmentedFace augmentedFace : augmentedFacesList) {
                if (isAdded)
                    return;
                // Create a face node and add it to the scene.
                AugmentedFaceNode augmentedFaceNode = new AugmentedFaceNode(augmentedFace);
                augmentedFaceNode.setParent(customArFragment.getArSceneView().getScene());
                augmentedFaceNode.setFaceRegionsRenderable(modelRenderable);   // Overlay the 3D assets on the face.

                isAdded = true;
            }
        });

        btnSetClickListener();


    }

    private String generateFilename() {

        String date =
                new SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.getDefault()).format(new Date());
        return Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + File.separator + "IM/" + date + "_screenshot.jpg";
    }

    private void saveBitmapToDisk(Bitmap bitmap, String filename) throws IOException {

        File out = new File(filename);
        if (!out.getParentFile().exists()) {
            out.getParentFile().mkdirs();
        }
        try (FileOutputStream outputStream = new FileOutputStream(filename);
             ByteArrayOutputStream outputData = new ByteArrayOutputStream()) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputData);
            outputData.writeTo(outputStream);
            outputStream.flush();
        } catch (IOException ex) {
            throw new IOException("Failed to save bitmap to disk", ex);
        }
    }

    private void takePhoto() {
        final String filename = generateFilename();
        ArSceneView arSceneView = customArFragment.getArSceneView();

        final Bitmap bitmap = Bitmap.createBitmap(arSceneView.getWidth(), arSceneView.getHeight(),
                Bitmap.Config.ARGB_8888);

        final HandlerThread handlerThread = new HandlerThread("PixelCopier");
        handlerThread.start();

        final Dialog alertDialog = new Dialog(ArActivity.this);
        alertDialog.setContentView(R.layout.alert_dialog_captured_image);
        ImageView imageView = alertDialog.findViewById(R.id.img_capturedImage);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            PixelCopy.request(arSceneView, bitmap, (copyResult) -> {
                if (copyResult == PixelCopy.SUCCESS) {

                    imageView.setImageBitmap(bitmap);
                    Button yes = alertDialog.findViewById(R.id.btn_keepImage_yes);
                    Button no = alertDialog.findViewById(R.id.btn_keepImage_no);
                    yes.setEnabled(true);
                    no.setEnabled(true);


                    yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {

                                saveBitmapToDisk(bitmap, filename);
                                Uri uri = Uri.parse("file://" + filename);
                                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                intent.setData(uri);
                                sendBroadcast(intent);

                            } catch (IOException e) {
                                Toast toast = Toast.makeText(ArActivity.this, e.toString(),
                                        Toast.LENGTH_LONG);
                                toast.show();
                                return;
                            }
                            alertDialog.cancel();
                            createSnackbar(filename);
                        }
                    });
                    no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.cancel();

                        }
                    });

                } else {
                    Toast toast = Toast.makeText(ArActivity.this,
                            "Nu a reușit salvarea capturii de ecran!: " + copyResult, Toast.LENGTH_LONG);
                    toast.show();
                }
                handlerThread.quitSafely();
            }, new Handler(handlerThread.getLooper()));
        }
        alertDialog.show();
    }

    public void createSnackbar(final String filename) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                "Imaginea de ecran a fost salvata", Snackbar.LENGTH_LONG);
        snackbar.setAction("Vezi în galerie", view1 -> {

            File photoFile = new File(filename);

            Uri photoURI = FileProvider.getUriForFile(ArActivity.this, "com.tibep.proiectlicenta.fileprovider",
                    photoFile);
            Intent intent = new Intent(Intent.ACTION_VIEW, photoURI);
            intent.setDataAndType(photoURI, "image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        });
        snackbar.show();
    }

    public void getPositionFromAdapter() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            image3DfromHome = extras.getInt("image3DfromHome");
        }

        Bundle extras2 = getIntent().getExtras();
        if (extras2 != null) {
            image3DfromSaved = extras.getInt("image3DfromSaved");
        }
    }

    public void btnSetClickListener() {
        Button captureButton = findViewById(R.id.btn_capture);

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStoragePermissionGranted();
                takePhoto();
            }
        });
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {


                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

}

