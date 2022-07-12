package com.zxfdwka.bestcountrymusic.ringtone.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.zxfdwka.bestcountrymusic.R;
import com.zxfdwka.bestcountrymusic.ringtone.Method.Methods;
import com.zxfdwka.bestcountrymusic.ringtone.SharedPref.Setting;

public class intActivity extends AppCompatActivity {

    Button btn;
    Methods methods;
    com.zxfdwka.bestcountrymusic.mp3.utils.Methods methods1;
    LinearLayout ll_ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_int_ringtone);

        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());

        methods1 = new com.zxfdwka.bestcountrymusic.mp3.utils.Methods(this);
        ll_ad = findViewById(R.id.ll_ad);
        methods1.showSMARTBannerAd(ll_ad);

        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main = new Intent(intActivity.this, SplashActivity.class);
                startActivity(main);
                finish();
            }
        });
    }


}
