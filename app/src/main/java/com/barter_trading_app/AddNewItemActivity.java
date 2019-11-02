package com.barter_trading_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class AddNewItemActivity extends AppCompatActivity  implements View.OnClickListener  {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_VIDEO_REQUEST = 2;

    private FirebaseAuth firebaseAuth;

    private ImageView imageViewNewItem;
    private ProgressBar progressBarNewItemImageUpload;
    private EditText editTextItemName;
    private EditText editTextItemDescription;
    private Spinner spinnerCategories;
    private Button buttonUploadNewItem;
    private Button buttonChooseVideo;

    private Uri imageUri;
    private Uri videoUri;
    private String uploadId;

    private DatabaseReference itemDatabaseReference;
    private StorageReference itemImageStorageReference;
    private StorageReference itemVideoStorageReference;

    private StorageTask uploadTask;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_item);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this,LoginActivity.class));
        }

        itemDatabaseReference = FirebaseDatabase.getInstance().getReference("uploadedItem");
        itemImageStorageReference = FirebaseStorage.getInstance().getReference("itemImages");
        itemVideoStorageReference = FirebaseStorage.getInstance().getReference("itemVideos");

        progressDialog = new ProgressDialog(this);

        imageViewNewItem = findViewById(R.id.imageViewNewItem);
        progressBarNewItemImageUpload = findViewById(R.id.progressBarNewItemImageUpload);
        editTextItemName = findViewById(R.id.editTextItemName);
        editTextItemDescription = findViewById(R.id.editTextItemDescription);
        spinnerCategories = findViewById(R.id.spinnerCategories);
        buttonUploadNewItem = findViewById(R.id.buttonUploadNewItem);
        buttonChooseVideo = findViewById(R.id.buttonChooseVideo);

        buttonChooseVideo.setOnClickListener(this);
        buttonUploadNewItem.setOnClickListener(this);
        imageViewNewItem.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v == buttonUploadNewItem){
            if(uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(getApplicationContext(), "Upload in progress...", Toast.LENGTH_LONG).show();
            }else {
                progressDialog.setMessage("Uploading New Item...");
                progressDialog.show();
                uploadFile();
            }
        }else if(v == imageViewNewItem){
            openFileChooser();
        }else if(v == buttonChooseVideo){
            openVideoChooser();
        }

    }

    private void openVideoChooser() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();
            Picasso.with(this).load(imageUri).into(imageViewNewItem);

        }else if(requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            videoUri = data.getData();
        }

    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if(imageUri != null){
            StorageReference fileReference = itemImageStorageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
            uploadTask = fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBarNewItemImageUpload.setProgress(0);
                        }
                    },500);

                    if (taskSnapshot.getMetadata() != null) {
                        if (taskSnapshot.getMetadata().getReference() != null) {
                            Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    String imageUrl = uri.toString();
                                    UploadedItem newItem = new UploadedItem(user.getUid(),editTextItemName.getText().toString(),imageUrl,spinnerCategories.getSelectedItem().toString(),editTextItemDescription.getText().toString());
                                    uploadId = itemDatabaseReference.push().getKey();
                                    itemDatabaseReference.child(uploadId).setValue(newItem);
                                    if(videoUri == null) {
                                        progressDialog.dismiss();
                                        finish();
                                    }
                                }
                            });
                        }
                    }


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Upload failed...", Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressBarNewItemImageUpload.setProgress((int) progress);
                }
            });
        }else{
            Toast.makeText(this, "No File selected...", Toast.LENGTH_LONG).show();
        }

        if(videoUri != null){
            StorageReference fileReference = itemVideoStorageReference.child(System.currentTimeMillis()+"."+getFileExtension(videoUri));
            uploadTask = fileReference.putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBarNewItemImageUpload.setProgress(0);
                        }
                    },500);

                    if (taskSnapshot.getMetadata() != null) {
                        if (taskSnapshot.getMetadata().getReference() != null) {
                            Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    String videoUrl = uri.toString();
                                    itemDatabaseReference.child(uploadId).child("itemVideoUrl").setValue(videoUrl);
                                    progressDialog.dismiss();
                                    finish();
                                }
                            });
                        }
                    }


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Upload failed...", Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressBarNewItemImageUpload.setProgress((int) progress);
                }
            });

        }

    }
}
