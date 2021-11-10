package com.example.pharmago.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.pharmago.R;

public class TermsAndPrivacyLandingPage extends AppCompatActivity {

    TextView tv_dp,tv_tnc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_privacy_landing_page);

        tv_dp = findViewById(R.id.tv_dp);
        tv_tnc = findViewById(R.id.tv_tnc);

        tv_tnc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TermsAndPrivacyLandingPage.this,TermsAndConditionActivity.class));
            }
        });
        tv_dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TermsAndPrivacyLandingPage.this,TermsAndPrivacyActivity.class));
            }
        });
    }
}