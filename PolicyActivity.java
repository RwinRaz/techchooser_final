package com.razani.techchooser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class PolicyActivity extends AppCompatActivity {

    private WebView webView;
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);
        webView = findViewById(R.id.policy_web);
mToolbar=findViewById(R.id.policy_tool_bar);
setSupportActionBar(mToolbar);
getSupportActionBar().setTitle("Policy");
getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        WebSettings webSetting = webView.getSettings();
        webSetting.setBuiltInZoomControls(true);
        webSetting.setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("file:///android_asset/policy.html");

    }
}