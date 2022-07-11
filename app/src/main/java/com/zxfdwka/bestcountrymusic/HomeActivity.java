package com.zxfdwka.bestcountrymusic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.zxfdwka.bestcountrymusic.databinding.ActivityHomeBinding;
import com.zxfdwka.bestcountrymusic.mp3.activity.LoginActivity;
import com.zxfdwka.bestcountrymusic.mp3.activity.ProfileActivity;
import com.zxfdwka.bestcountrymusic.mp3.activity.SettingActivity;
import com.zxfdwka.bestcountrymusic.mp3.utils.Constant;
import com.zxfdwka.bestcountrymusic.ringtone.Activity.MainActivity;
import com.zxfdwka.bestcountrymusic.ringtone.Activity.SplashActivity;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.ringtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        binding.mp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, com.zxfdwka.bestcountrymusic.mp3.activity.MainActivity.class);
                startActivity(intent);
            }
        });

        binding.setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        binding.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constant.isLogged) {
                    startActivity(new Intent(HomeActivity.this, BaseFavoriteActivity.class));
                }
                else{
                    Toast.makeText(HomeActivity.this, "Please login first", Toast.LENGTH_LONG).show();
                }


            }
        });

        binding.user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constant.isLogged){
                    startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                }
                else{
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                }

            }
        });
    }


}