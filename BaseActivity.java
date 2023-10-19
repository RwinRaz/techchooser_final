package com.razani.techchooser;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class BaseActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ProgressDialog mNet;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mNet=new ProgressDialog(this);
        everyThingsRight();
    }
    private void everyThingsRight()
    {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser==null)
        {
            startActivity(new Intent(BaseActivity.this,StartActivity.class));
            finish();
        }
        isNetworkAvailable();
    }
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean mIs=activeNetworkInfo != null && activeNetworkInfo.isConnected();
        if(!mIs)
        {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Network");
        builder.setMessage("This app needs Internet connection. Please make sure your phone is connected to stable network.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();}
        return mIs;
    }

    @Override
    protected void onResume() {
        super.onResume();
        everyThingsRight();
    }
}
