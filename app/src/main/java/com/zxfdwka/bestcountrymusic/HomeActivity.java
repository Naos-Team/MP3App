package com.zxfdwka.bestcountrymusic;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.zxfdwka.bestcountrymusic.databinding.ActivityHomeBinding;
import com.zxfdwka.bestcountrymusic.mp3.activity.LoginActivity;
import com.zxfdwka.bestcountrymusic.mp3.activity.ProfileActivity;
import com.zxfdwka.bestcountrymusic.mp3.activity.SettingActivity;
import com.zxfdwka.bestcountrymusic.mp3.interfaces.InterScreenListener;
import com.zxfdwka.bestcountrymusic.mp3.utils.Constant;
import com.zxfdwka.bestcountrymusic.mp3.utils.Methods;
import com.zxfdwka.bestcountrymusic.radio.activity.RadioBaseActivity;
import com.zxfdwka.bestcountrymusic.ringtone.Activity.MainActivity;

public class HomeActivity extends AppCompatActivity {
    private Methods methods;
    private ActivityHomeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        MethodsAll.getInstance().setContext(HomeActivity.this);
        MethodsAll.getInstance().startCountdown();

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
                if (Constant.isLogged) {
                    methods.showInterScreenAd(new InterScreenListener() {
                        @Override
                        public void onClick() {
                            startActivity(new Intent(HomeActivity.this, BaseFavoriteActivity.class));
                        }
                    });
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

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alert;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alert = new AlertDialog.Builder(HomeActivity.this, R.style.ThemeDialog);
        } else {
            alert = new AlertDialog.Builder(HomeActivity.this);
        }

        alert.setTitle(getString(R.string.exit));
        alert.setMessage(getString(R.string.sure_exit));
        alert.setPositiveButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        alert.show();
//        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MethodsAll.getInstance().setContext(HomeActivity.this);
//        MethodsAll.getInstance().startCountdown();
    }
}