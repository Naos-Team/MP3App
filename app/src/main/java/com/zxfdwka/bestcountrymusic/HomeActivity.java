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
import com.zxfdwka.bestcountrymusic.mp3.interfaces.InterScreenListener;
import com.zxfdwka.bestcountrymusic.mp3.utils.Constant;
import com.zxfdwka.bestcountrymusic.mp3.utils.Methods;
import com.zxfdwka.bestcountrymusic.radio.activity.RadioBaseActivity;
import com.zxfdwka.bestcountrymusic.ringtone.Activity.MainActivity;
import com.zxfdwka.bestcountrymusic.ringtone.Activity.SplashActivity;

public class HomeActivity extends AppCompatActivity {
    private Methods methods;
    private ActivityHomeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        methods = new Methods(this);

        methods.showSMARTBannerAd(binding.adView);

        binding.ringtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                methods.showInterScreenAd(new InterScreenListener() {
                    @Override
                    public void onClick() {
                        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });

            }
        });

        binding.radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                methods.showInterScreenAd(new InterScreenListener() {
                    @Override
                    public void onClick() {
                        Intent intent = new Intent(HomeActivity.this, RadioBaseActivity.class);
                        startActivity(intent);
                    }
                });

            }
        });


        binding.mp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 methods.showInterScreenAd(new InterScreenListener() {
                     @Override
                     public void onClick() {
                         Intent intent = new Intent(HomeActivity.this, com.zxfdwka.bestcountrymusic.mp3.activity.MainActivity.class);
                         startActivity(intent);
                     }
                 });

            }
        });

        binding.setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                methods.showInterScreenAd(new InterScreenListener() {
                    @Override
                    public void onClick() {
                        Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
                        startActivity(intent);
                    }
                });

            }
        });

        binding.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (Constant.isLogged) {
//                    methods.showInterScreenAd(new InterScreenListener() {
//                        @Override
//                        public void onClick() {
//                            startActivity(new Intent(HomeActivity.this, BaseFavoriteActivity.class));
//                        }
//                    });
//                }
//                else{
//                    Toast.makeText(HomeActivity.this, "Please login first", Toast.LENGTH_LONG).show();
//                }
                startActivity(new Intent(HomeActivity.this, BaseFavoriteActivity.class));



            }
        });

        binding.user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constant.isLogged){
                    methods.showInterScreenAd(new InterScreenListener() {
                        @Override
                        public void onClick() {
                            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));

                        }
                    });
                }
                else{
                    methods.showInterScreenAd(new InterScreenListener() {
                        @Override
                        public void onClick() {
                            startActivity(new Intent(HomeActivity.this, LoginActivity.class));

                        }
                    });
                }

            }
        });

        binding.layoutPremium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                methods.showInterScreenAd(new InterScreenListener() {
                    @Override
                    public void onClick() {
                        startActivity(new Intent(HomeActivity.this, PurchaseActivity.class));

                    }
                });
            }
        });
    }


}