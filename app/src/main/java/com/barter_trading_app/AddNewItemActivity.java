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

    private FirebaseAuth firebaseAuth;

    private ImageView imageViewNewItem;
    private ProgressBar progressBarNewItemImageUpload;
    private EditText editTextItemName;
    private EditText editTextItemDescription;
    private Spinner spinnerCategories;
    private Button buttonUploadNewItem;

    private Uri imageUri;

    private DatabaseReference itemDatabaseReference;
    private StorageReference itemImageStorageReference;

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

        progressDialog = new ProgressDialog(this);

        imageViewNewItem = findViewById(R.id.imageViewNewItem);
        progressBarNewItemImageUpload = findViewById(R.id.progressBarNewItemImageUpload);
        editTextItemName = findViewById(R.id.editTextItemName);
        editTextItemDescription = findViewById(R.id.editTextItemDescription);
        spinnerCategories = findViewById(R.id.spinnerCategories);
        buttonUploadNewItem = findViewById(R.id.buttonUploadNewItem);

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
        }

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
                    Toast.makeText(getApplicationContext(), "Upload successful...", Toast.LENGTH_LONG).show();

                    if (taskSnapshot.getMetadata() != null) {
                        if (taskSnapshot.getMetadata().getReference() != null) {
                            Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    String imageUrl = uri.toString();
                                    UploadedItem newItem = new UploadedItem(user.getUid(),editTextItemName.getText().toString(),imageUrl,spinnerCategories.getTransitionName(),editTextItemDescription.getText().toString());
                                    String uploadId = itemDatabaseReference.push().getKey();
                                    itemDatabaseReference.child(uploadId).setValue(newItem);
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
        }else{
            Toast.makeText(this, "No File selected...", Toast.LENGTH_LONG).show();
        }
    }
}
