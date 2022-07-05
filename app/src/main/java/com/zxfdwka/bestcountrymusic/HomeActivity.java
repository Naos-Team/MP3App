package com.zxfdwka.bestcountrymusic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.zxfdwka.bestcountrymusic.mp3.activity.R;
import com.zxfdwka.bestcountrymusic.mp3.activity.databinding.ActivityHomeBinding;
import com.zxfdwka.bestcountrymusic.ringtone.Activity.SplashActivity;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.icOpenRingtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SplashActivity.class);
                startActivity(intent);
            }
        });
    }


}