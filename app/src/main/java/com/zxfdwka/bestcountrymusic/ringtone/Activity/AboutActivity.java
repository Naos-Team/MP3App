package com.zxfdwka.bestcountrymusic.ringtone.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.zxfdwka.bestcountrymusic.R;
import com.zxfdwka.bestcountrymusic.ringtone.Method.Methods;
import com.zxfdwka.bestcountrymusic.ringtone.SharedPref.Setting;
public class AboutActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView  company, email, website, contact;
    Methods methods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Setting.Dark_Mode) {
            setTheme(R.style.AppTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_ringtone);

        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        company = (TextView) findViewById(R.id.company);
        email = (TextView) findViewById(R.id.email);
        website = (TextView) findViewById(R.id.website);
        contact = (TextView) findViewById(R.id.contact);


        company.setText(Setting.company);
        email.setText(Setting.email);
        website.setText(Setting.website);
        contact.setText(Setting.contact);

    }


}