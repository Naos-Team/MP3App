package com.zxfdwka.bestcountrymusic;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.zxfdwka.bestcountrymusic.databinding.ActivityHomeBinding;
import com.zxfdwka.bestcountrymusic.databinding.ActivityPurchaseBinding;
import com.zxfdwka.bestcountrymusic.mp3.interfaces.InterScreenListener;
import com.zxfdwka.bestcountrymusic.mp3.utils.Methods;

public class PurchaseActivity extends AppCompatActivity {
    ActivityPurchaseBinding binding;
    Methods methods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPurchaseBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        methods = new Methods(this);

        binding.icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                methods.showInterScreenAd(new InterScreenListener() {
                    @Override
                    public void onClick() {
                        finish();
                    }
                });
            }
        });
    }
}