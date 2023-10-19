package com.razani.techchooser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

import static android.widget.ImageView.ScaleType.FIT_XY;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout mRegName, mRegEmail, mRegPassword, mRegRePass;
    private Button mRegCreateBtn;
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private ProgressDialog mRegProgress;
    private DatabaseReference database;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        //      ImageView img = findViewById(R.id.spinning_wheel_image);
//        img.setScaleType(FIT_XY);
//        img.setBackgroundResource(R.drawable.spin_animation);
//        img.setAlpha((float) 0.10);
        // Get the background, which has been compiled to an AnimationDrawable object.
//        AnimationDrawable frameAnimation = (AnimationDrawable) img.getBackground();

        // Start the animation (looped playback by default).
//        frameAnimation.start();
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Register New Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRegCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mRegName.getEditText().getText().toString();
                String email = mRegEmail.getEditText().getText().toString();
                String password = mRegPassword.getEditText().getText().toString();
                if(checkBox.isChecked()) {
                    createAccount(username, email, password);
                }
                else {
                    Toast.makeText(RegisterActivity.this, "You must agree with terms of service.", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void init() {
        mRegCreateBtn = findViewById(R.id.reg_create_btn);
        mRegName = findViewById(R.id.reg_username_et);
        mRegEmail = findViewById(R.id.reg_email_et);
        mRegPassword = findViewById(R.id.reg_password_et);
        mRegRePass = findViewById(R.id.reg_repass_et);
        mAuth = FirebaseAuth.getInstance();
        checkBox = findViewById(R.id.team_check);
        mRegProgress = new ProgressDialog(this);
        mToolbar = findViewById(R.id.register_page_toolbar);
    }

    private void createAccount(final String user_name, String email, String password) {

        if (valUserName(mRegName.getEditText().getText().toString()) && valEmail(mRegEmail.getEditText().getText().toString()) && valPassword(mRegPassword.getEditText().getText().toString())
                && valRePass(mRegPassword.getEditText().getText().toString(), mRegRePass.getEditText().getText().toString())

        ) {
//                    if (mRegRePass.getEditText().getText().toString().equals(mRegPassword.getEditText().getText().toString()))
//                    {
//                        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
//                        {


            mRegProgress.setTitle("Registering");
            mRegProgress.setMessage("Please with while we create your account....");
            mRegProgress.setCanceledOnTouchOutside(false);
            mRegProgress.show();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                        String uid = current_user.getUid();
                        database = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference myRef = database.child("Users").child(uid);
                        HashMap<String, String> infoHashMap = new HashMap<>();
                        infoHashMap.put("username", user_name);
                        infoHashMap.put("status", "member");
                        infoHashMap.put("device_token", FirebaseInstanceId.getInstance().getToken());
                        //infoHashMap.put("image", null);
                        myRef.setValue(infoHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    mRegProgress.dismiss();
                                } else {
                                    mRegProgress.hide();
                                    Toast.makeText(RegisterActivity.this, task.getException().toString(),
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                       // Log.d("Reg", "createUserWithEmail:success");

                        startActivity(new Intent(RegisterActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish();


                    } else {
                        mRegProgress.hide();
                        DatabaseReference error=FirebaseDatabase.getInstance().getReference().child("error").push();
                        Map messageMap = new HashMap();
                        messageMap.put("message", task.getException().getMessage());
                        error.updateChildren(messageMap);
                        //Toast.makeText(RegisterActivity.this, "There is some problem with your registration please try again later.",
                       //         Toast.LENGTH_LONG).show();
                        Toast.makeText(RegisterActivity.this, task.getException().getMessage(),
                                         Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
    }

    private boolean valUserName(String s) {
        if (s.isEmpty() || s.length() < 3) {
            mRegName.setError("your name is to short");
            return false;
        } else {
            mRegName.setError(null);
            return true;
        }
    }

    private boolean valEmail(String s) {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
            mRegEmail.setError("Please enter a valid Email.");
            return false;
        } else {
            mRegEmail.setError(null);
            return true;
        }


    }

    private boolean valPassword(String s) {
        if (s.isEmpty() || s.length() < 6) {
            mRegPassword.setError("Your Password must be at least 6 character.");
            return false;
        } else {
            mRegPassword.setError(null);
            return true;
        }
    }

    private boolean valRePass(String s1, String s2) {
        if (s1.equals(s2)) {
            mRegRePass.setError(null);
            return true;
        } else {
            mRegRePass.setError("Your Password and Conform Password are NOT same");
            return false;

        }
    }

    public void onPolicyClick(View view) {
        startActivity(new Intent(RegisterActivity.this,PolicyActivity.class));
    }
}


