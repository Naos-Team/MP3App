package com.naosteam.countrymusic;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.naosteam.countrymusic.databinding.ActivityHomeBinding;
import com.naosteam.countrymusic.mp3.activity.LoginActivity;
import com.naosteam.countrymusic.mp3.activity.ProfileActivity;
import com.naosteam.countrymusic.mp3.activity.SettingActivity;
import com.naosteam.countrymusic.mp3.interfaces.InterScreenListener;
import com.naosteam.countrymusic.mp3.utils.Constant;
import com.naosteam.countrymusic.mp3.utils.Methods;
import com.naosteam.countrymusic.radio.activity.RadioBaseActivity;
import com.naosteam.countrymusic.ringtone.Activity.MainActivity;

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
                        Intent intent = new Intent(HomeActivity.this, com.naosteam.countrymusic.mp3.activity.MainActivity.class);
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