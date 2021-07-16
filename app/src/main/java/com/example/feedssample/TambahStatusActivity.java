package com.example.feedssample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Placeholder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class TambahStatusActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;

    ConstraintLayout constraintLayout;
    Placeholder placeholder;

    Button btnBack, btnGalery, btnKamera, btnFile, btnPosting;
    EditText textStatus;
    ProgressBar progressBar;

    private Uri filePath;
    private String fotoUrl;

    private static final int IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_status);

        constraintLayout = findViewById(R.id.layoutPost);
        placeholder = findViewById(R.id.holderGambar);

        textStatus = findViewById(R.id.et_Status);
        btnBack = findViewById(R.id.btnBack);
        btnGalery = findViewById(R.id.btnGalery);
        btnKamera = findViewById(R.id.btnKamera);
        btnFile = findViewById(R.id.btnFile);
        btnPosting = findViewById(R.id.btnPost);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        btnGalery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ambilGambar();
            }
        });

        btnPosting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    public void swapView(View view){
        TransitionManager.beginDelayedTransition(constraintLayout);
        placeholder.setContentId(view.getId());
    }

    private void ambilGambar(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Pilih Gambar"),IMAGE_REQUEST);
    }

    private void SimpanData(String nama, String telepon, String sosmed, String alamat, String foto){

        Map<String, Object> contactData = new HashMap<>();

        contactData.put("nama", nama);
        contactData.put("telepon", telepon);
        contactData.put("sosmed", sosmed);
        contactData.put("alamat", alamat);
        contactData.put("foto", foto);

        firebaseFirestore.collection("Contacts").document(telepon).set(contactData).isSuccessful();
    }

    private void uploadImage(){
        if(filePath != null){
            final StorageReference ref = storageReference.child(textStatus.getText().toString());
            UploadTask uploadTask = ref.putFile(filePath);

            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri imagePath = task.getResult();

                    fotoUrl = imagePath.toString();
                    SimpanData(TextNama.getText().toString(),
                            TextTelepon.getText().toString(),
                            TextSosmed.getText().toString(),
                            TextAlamat.getText().toString(),
                            fotoUrl);

                    progressBar.setProgress(0);
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(ActivityTambahContact.this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    progressBar.setVisibility(View.VISIBLE);
                    double progres = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    progressBar.setProgress((int)progres);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(ActivityTambahContact.this, "Failed " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

}
