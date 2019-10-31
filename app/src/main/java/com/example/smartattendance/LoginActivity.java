package com.example.smartattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {

    private EditText userEmail,userPassword;
    private Toolbar loginToolbar;
    private Button loginButton;
    private String email,password;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupUIViews();
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Signing In");
        progressDialog.setMessage("Please wait while we verify your credentials...");
        progressDialog.setCanceledOnTouchOutside(false);

        //get data
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email=userEmail.getText().toString();
                password=userPassword.getText().toString();
                progressDialog.show();

                //validate
                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                    Toast.makeText(LoginActivity.this,"Please fill all details",Toast.LENGTH_LONG).show();
                    return;
                }
                if(Patterns.EMAIL_ADDRESS.matcher(email).matches()==false){
                    Toast.makeText(LoginActivity.this,"Please provide email",Toast.LENGTH_LONG).show();
                    return;
                }
                if(password.length()<8){
                    Toast.makeText(LoginActivity.this,"Password should be atleast 8 characters long",Toast.LENGTH_LONG).show();
                    return;
                }

                firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            System.out.println("hii");
                            progressDialog.dismiss();
                            //Toast.makeText(LoginActivity.this,"Successfully Logged In",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(LoginActivity.this,DashboardActivity.class));
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    private void setupUIViews() {
        userEmail=(EditText) findViewById(R.id.userEmailAddress);
        userPassword=(EditText)findViewById(R.id.userPassword);
        loginButton=(Button)findViewById(R.id.loginButton);
        loginToolbar=(Toolbar)findViewById(R.id.dashToolbar);
        //setSupportActionBar(loginToolbar);

        firebaseAuth=FirebaseAuth.getInstance();

    }

}
