package com.razani.techchooser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {
    // private Toolbar mToolBar;
    private ProgressDialog mProgress;
    private EditText mLoginPassword, mLoginEmail;
    private Button mLoginBtn;
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private boolean mForResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        mForResult = getIntent().getBooleanExtra("ForResult", false);
        //setSupportActionBar(mToolBar);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setTitle("Login");
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mLoginEmail.getText().toString();
                if (mLoginPassword.getVisibility()!=View.GONE) {
                    String password = mLoginPassword.getText().toString();
                    if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {
                        mProgress.setTitle("Logging In");
                        mProgress.setMessage("Please with while we check your identity... ");
                        mProgress.setCanceledOnTouchOutside(false);
                        mProgress.show();
                        loginUser(email, password);
                    } else {
                        Toast.makeText(LoginActivity.this, "Your Email or Password is wrong.", Toast.LENGTH_LONG).show();
                    }

                }
                else
                {
                    email=mLoginEmail.getText().toString();
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email);
                    Toast.makeText(LoginActivity.this,"We sent a reset password link for "+email+".",Toast.LENGTH_LONG).show();
                }
            }
        });
        if (mForResult) {
            mLoginEmail.setText(getIntent().getStringExtra("email"));
            mLoginEmail.setEnabled(false);
        }

    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mProgress.dismiss();
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();
                            mRef.child(user.getUid()).child("device_token").setValue(FirebaseInstanceId.getInstance().getToken());
                            if (mForResult) {
                                Intent returnIntent = new Intent();
                                setResult(Activity.RESULT_OK, returnIntent);
                                finish();
                            } else {
                               SharedPreferences mPref = getSharedPreferences("mPref", MODE_PRIVATE);
                                mPref.edit().putBoolean("firstOrder",false).apply();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                finish();
                            }
                        } else {
                            mProgress.hide();
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();
        mLoginBtn = findViewById(R.id.login_entry_btn);
        // mToolBar=findViewById(R.id.login_toolbar);
        mLoginPassword = findViewById(R.id.login_password_et);
        mLoginEmail = findViewById(R.id.login_email_et);
        mRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mProgress = new ProgressDialog(this);
    }

    public void onForgetClick(View view) {
        mLoginPassword.setVisibility(View.GONE);
        Button forget=findViewById(R.id.login_forget);
        forget.setVisibility(View.GONE);
        mLoginBtn.setText("Send An Email");

    }
}