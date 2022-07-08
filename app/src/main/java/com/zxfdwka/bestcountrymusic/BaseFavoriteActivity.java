package com.zxfdwka.bestcountrymusic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.zxfdwka.bestcountrymusic.databinding.ActivityBaseCategoriesBinding;
import com.zxfdwka.bestcountrymusic.databinding.ActivityBaseFavoriteBinding;
import com.zxfdwka.bestcountrymusic.databinding.ActivityHomeBinding;
import com.zxfdwka.bestcountrymusic.ringtone.Activity.FavouriteActivity;

public class BaseFavoriteActivity extends AppCompatActivity {
    ActivityBaseFavoriteBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBaseFavoriteBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.lRingtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BaseFavoriteActivity.this, FavouriteActivity.class));
            }
        });
    }
}