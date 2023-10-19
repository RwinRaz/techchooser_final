package com.razani.techchooser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountActivity extends BaseActivity {

    private LinearLayout mChangePassLayout;
    private Button mButton;
    private TextView mName, mPassword, mEmail;
    private TextInputLayout mAccountPassword, mAccountRepass;
    private Toolbar mToolBar;
    private DatabaseReference mRef;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        init();
        mChangePassLayout.setVisibility(View.GONE);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Account Setting");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void init() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        mToolBar = findViewById(R.id.account_tool_bar);
        mName = findViewById(R.id.account_name_tv);
        mPassword = findViewById(R.id.account_password_tv);
        mEmail = findViewById(R.id.account_email_tv);
        mChangePassLayout = findViewById(R.id.change_layout);
        mButton = findViewById(R.id.account_change_btn);
        mRef.child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mName.setText(mName.getText() + " " + dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mEmail.setText(user.getEmail());
    }

    private boolean valPassword(String s) {
        if (s.isEmpty() || s.length() < 6) {
            mAccountPassword.setError("Your Password must be at least 6 character.");
            return false;
        } else {
            mAccountPassword.setError(null);
            return true;
        }
    }

    private boolean valRePass(String s1, String s2) {
        if (s1.equals(s2)) {
            mAccountRepass.setError(null);
            return true;
        } else {
            mAccountRepass.setError("Your Password and Conform Password are NOT same");
            return false;

        }
    }

    public void onChangeOpener(View view) {
        if(isNetworkAvailable()) {
            Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
            intent.putExtra("ForResult", true).putExtra("email", mEmail.getText().toString());
            startActivityForResult(intent, 1);
        }
    }

    public void onChangePassword(View view) {
        if(isNetworkAvailable()) {
            if (valPassword(mAccountPassword.getEditText().getText().toString()) &&
                    valRePass(mAccountPassword.getEditText().getText().toString(), mAccountRepass.getEditText().getText().toString())) {

                String newPassword = mAccountPassword.getEditText().getText().toString();

                user.updatePassword(newPassword)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(AccountActivity.this, "Password changed", Toast.LENGTH_LONG).show();
                                    mChangePassLayout.setVisibility(View.GONE);
                                    mButton.setVisibility(View.VISIBLE);
                                } else {
                                    Toast.makeText(AccountActivity.this, "Password NOT changed", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                mAccountRepass = findViewById(R.id.account_repass_et);
                mAccountPassword = findViewById(R.id.account_password_et);
                mChangePassLayout.setVisibility(View.VISIBLE);
                mButton.setVisibility(View.GONE);

            }
        }
    }
}