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
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        methods = new Methods(this);

        sharedPreferences = getSharedPreferences("save_time_use", Context.MODE_PRIVATE);
        Constant.use_app_time = sharedPreferences.getInt("time_use", 0);

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
                int new_time;

                if(Constant.use_app_time == Constant.time_to_rate &&
                        !sharedPreferences.getBoolean("rated", false)) {
                    new_time = 0;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("time_use", new_time);
                    editor.commit();
                    Dialog dialog = new Dialog(HomeActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.layout_dialog_rate);
                    Window window = dialog.getWindow();
                    if (window == null) {
                        return;
                    }
                    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    WindowManager.LayoutParams windowAttributes = window.getAttributes();
                    windowAttributes.gravity = Gravity.CENTER;
                    dialog.setCancelable(true);
                    RatingBar ratingBar = dialog.findViewById(R.id.ratingBar);
                    Button btn_cancel_dialog = dialog.findViewById(R.id.btn_cancel_dialog);
                    btn_cancel_dialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            finish();
                        }
                    });

                    Button btn_change_dialog = dialog.findViewById(R.id.btn_change_dialog);
                    btn_change_dialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int rating = (int) ratingBar.getRating();
                            editor.putBoolean("rated", true);
                            editor.commit();
                            if (rating >= 3) {
                                rateApp();
                            } else {
                            }
                            dialog.dismiss();
                            finish();
                        }
                    });
                    dialog.show();
                } else {
                    new_time = (Constant.use_app_time  < Constant.time_to_rate) ? Constant.use_app_time + 1 : 0;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("time_use", new_time);
                    editor.commit();
                    finish();
                }
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

    public void rateApp()
    {
        try
        {
            Intent rateIntent = rateIntentForUrl("market://details");
            startActivity(rateIntent);
        }
        catch (ActivityNotFoundException e)
        {
            Intent rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details");
            startActivity(rateIntent);
        }
    }

    private Intent rateIntentForUrl(String url)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, getPackageName())));
        intent.putExtra(Intent.EXTRA_SUBJECT,"AAA" );
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21)
        {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        }
        else
        {
            //noinspection deprecation
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    }
}