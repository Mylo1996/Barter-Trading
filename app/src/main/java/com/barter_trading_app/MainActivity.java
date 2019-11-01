package com.barter_trading_app;

import android.app.ProgressDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextFirstName;
    private EditText editTextSureName;
    private EditText editTextPhoneNumber;
    private TextView textViewSignIn;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference userDatabaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(),WelcomeActivity.class));
        }

        progressDialog = new ProgressDialog(this);

        buttonRegister = (Button) findViewById(R.id.buttonRegister);

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextFirstName = (EditText) findViewById(R.id.editTextFirstName);
        editTextSureName = (EditText) findViewById(R.id.editTextSureName);
        editTextPhoneNumber = (EditText) findViewById(R.id.editTextPhoneNumber);

        textViewSignIn = (TextView) findViewById(R.id.textViewSignIn);

        buttonRegister.setOnClickListener(this);
        textViewSignIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == buttonRegister){
            registerUser();
        }else if(v == textViewSignIn){
            // open LoginActivity
            finish();
            startActivity(new Intent(this,LoginActivity.class));
        }

    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){ // Check if e-mail is empty
            Toast.makeText(this,"Please enter an e-mail address",Toast.LENGTH_LONG).show();
            return; //Stop the function execution further.
        }
        if(TextUtils.isEmpty(password)){ // Check if password is empty
            Toast.makeText(this,"Please enter a password address",Toast.LENGTH_LONG).show();
            return;
        }

        //if the input values are valid
        //show a progressbar because the Internet connection.
        progressDialog.setMessage("Registering User...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    userDatabaseReference = FirebaseDatabase.getInstance().getReference("userdata");

                    String firstName = editTextFirstName.getText().toString();
                    String sureName = editTextSureName.getText().toString();
                    String phoneNumber = editTextPhoneNumber.getText().toString();

                    UserData userData = new UserData(firstName,sureName,phoneNumber);

                    FirebaseUser user = firebaseAuth.getCurrentUser();

                    userDatabaseReference.child(user.getUid()).setValue(userData);
                    progressDialog.dismiss();
                    //User successfully registered and logged in
                    //Start the ProfileActivity

                    finish();
                    startActivity(new Intent(getApplicationContext(),WelcomeActivity.class));
                    Toast.makeText(MainActivity.this, "Registration successful!", Toast.LENGTH_LONG).show();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Registration failed! Please try again", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
