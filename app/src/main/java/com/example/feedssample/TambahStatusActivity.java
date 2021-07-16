package com.example.feedssample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Placeholder;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class TambahStatusActivity extends AppCompatActivity {

    public final int SELECT_FILE = 1;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;


    ConstraintLayout constraintLayout;
    Intent intent;

    Button btnBack, btnGalery, btnKamera, btnFile, btnPosting;
    EditText textStatus;
    ImageView imgPreview;
    ProgressBar progressBar;
    CircleImageView userProfile;
    TextView userName;

    String fileGambarPath;

    Bitmap bitmap, decoded;
    Uri fileUri;
    int bitmap_size = 40; // image quality 1 - 100;
    int max_resolution_image = 800;

    private Uri filePath;
    private String profileUrl, fotoUrl;

    private static final int IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_status);

        constraintLayout = findViewById(R.id.layoutPost);

        textStatus = findViewById(R.id.et_Status);
        btnBack = findViewById(R.id.btnBack);
        btnGalery = findViewById(R.id.btnGalery);
        btnKamera = findViewById(R.id.btnKamera);
        btnFile = findViewById(R.id.btnFile);
        btnPosting = findViewById(R.id.btnPost);
        imgPreview = findViewById(R.id.holderGambar);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        btnGalery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        btnPosting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void selectImage() {
        imgPreview.setImageResource(0);
        intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "selectPicture"), SELECT_FILE);
    }



    private void SimpanData(String userProfil , String userName, String status, String fotoPost){

        Map<String, Object> statusData = new HashMap<>();

        statusData.put("userProfile", userProfil);
        statusData.put("userName", userName);
        statusData.put("status", status);
        statusData.put("fotoPost", fotoPost);

        firebaseFirestore.collection("Status").document(userName).set(statusData).isSuccessful();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("onActivityResult", "requestCode " + requestCode + ", resultCode " + resultCode);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE && data != null && data.getData() != null){
                try {
                    // mengambil gambar dari Gallery
                    bitmap = MediaStore.Images.Media.getBitmap(TambahStatusActivity.this.getContentResolver(), data.getData());
                    setToImageView(getResizedBitmap(bitmap, max_resolution_image));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(this, "Anda belum mengambil gambar",
                    Toast.LENGTH_LONG).show();
        }
    }

    // Untuk menampilkan bitmap pada ImageView
    private void setToImageView(Bitmap bmp) {
        //compress image
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, bytes);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));

        //menampilkan gambar yang dipilih dari camera/gallery ke ImageView
        imgPreview.setImageBitmap(decoded);
    }

    // Untuk resize bitmap
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    private static File getOutputMediaFile() {

        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "DeKa");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e("Monitoring", "Oops! Failed create Monitoring directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_DeKa_" + timeStamp + ".jpg");

        return mediaFile;
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
                    Uri userProfile = task.getResult();
                    Uri postFoto = task.getResult();

                    profileUrl = userProfile.toString();
                    fotoUrl = postFoto.toString();
                    SimpanData(profileUrl,
                            userName.getText().toString(),
                            textStatus.getText().toString(),
                            fotoUrl);

                    progressBar.setProgress(0);
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(TambahStatusActivity.this, "Status Berhasil Diposting", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(TambahStatusActivity.this, "Gagal Memposting Status. " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

}
