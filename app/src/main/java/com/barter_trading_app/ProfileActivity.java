package com.barter_trading_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Map;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imageViewProfile;
    private Button buttonUploadImage;
    private ProgressBar progressBarUploadImage;

    private Uri imageUri;

    private FirebaseAuth firebaseAuth;

    private TextView textViewName;
    private TextView textViewRating;
    private RatingBar ratingBar;
    private Button buttonLogout;



    private Button buttonOtherUser;



    private DatabaseReference userDatabaseReference;
    private StorageReference userImageStorageReference;

    private StorageTask uploadTask;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this,LoginActivity.class));
        }

        userDatabaseReference = FirebaseDatabase.getInstance().getReference("userdata");
        userImageStorageReference = FirebaseStorage.getInstance().getReference("userimages");

        imageViewProfile = findViewById(R.id.imageViewProfile);
        buttonUploadImage = findViewById(R.id.buttonUploadImage);
        progressBarUploadImage = findViewById(R.id.progressBarImageUpload);

        textViewName = findViewById(R.id.textViewName);
        textViewRating = findViewById(R.id.textViewRating);
        ratingBar = findViewById(R.id.ratingBar);

        buttonLogout = findViewById(R.id.buttonLogout);



        /*//////////////////////////////////////
                    TEST
        //////////////////////////////////////*/

        buttonOtherUser = findViewById(R.id.buttonOtherUser);
        buttonOtherUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SelectedProfileActivity.class);
                intent.putExtra("USER_ID", "Zo5cpT7ed6WkgRbRF4rntlX9qb52");
                startActivity(intent);
            }
        });

        ////////////////////////////////////////



        buttonLogout.setOnClickListener(this);
        imageViewProfile.setOnClickListener(this);
        buttonUploadImage.setOnClickListener(this);


    }




    @Override
    protected void onStart() {
        super.onStart();
        ValueEventListener userDataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get UserData object and use the values to update the UI
                FirebaseUser user = firebaseAuth.getCurrentUser();
                UserData userData = dataSnapshot.child(user.getUid()).getValue(UserData.class);
                textViewName.setText(userData.firstName+" "+userData.sureName+"\n"+user.getEmail()+"\n" +userData.flagList.size()+" Flags");

                float avg=0;
                float sum=0;
                float count = 0;
                for (Map.Entry<String, Integer> entry : userData.rating.entrySet()) {

                    System.err.println(entry.getValue());
                    sum+=entry.getValue();
                    count++;
                    avg = sum/count;

                }
                System.err.println(avg);
                ratingBar.setRating(avg);
                ratingBar.setIsIndicator(true);
                textViewRating.setText((Math.round(avg * 10) / 10.0)+"/5.0");
                Picasso.with(getApplicationContext()).load(userData.profileImageUrl).into(imageViewProfile);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting UserData failed
            }
        };
        userDatabaseReference.addValueEventListener(userDataListener);

    }


    @Override
    public void onClick(View v) {
        if(v == buttonLogout){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this,LoginActivity.class));
        }else if(v == buttonUploadImage){
            if(uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(getApplicationContext(), "Upload in progress...", Toast.LENGTH_LONG).show();
            }else {
                uploadFile();
            }
        }else if(v == imageViewProfile){
            openFileChooser();
        }

    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if(imageUri != null){
            StorageReference fileReference = userImageStorageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
            uploadTask = fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBarUploadImage.setProgress(0);
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
                                    userDatabaseReference.child(user.getUid()).child("profileImageUrl").setValue(imageUrl);
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
                    progressBarUploadImage.setProgress((int) progress);
                }
            });
        }else{
            Toast.makeText(this, "No File selected...", Toast.LENGTH_LONG).show();
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
            Picasso.with(this).load(imageUri).into(imageViewProfile);

        }
    }
}
