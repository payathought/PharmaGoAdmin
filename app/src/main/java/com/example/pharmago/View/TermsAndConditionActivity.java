package com.example.pharmago.View;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.pharmago.MainActivity;
import com.example.pharmago.R;

public class TermsAndConditionActivity extends AppCompatActivity {

    Thread timer;

    ProgressDialog progressDialog;
    WebView webview;
    private static final String TAG = "TermsAndConditionActivi";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_condition);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Pharma GO");
        progressDialog.setMessage("It will take a moment");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);


        webview = findViewById(R.id.webview);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webview.loadUrl("file:///android_asset/termsandcontion.html");


    }

}