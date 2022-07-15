package com.naosteam.countrymusic;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.naosteam.countrymusic.mp3.utils.SharedPref;

public class MethodsAll {
    private Dialog dialog, dialog_upgrade;
    private Context context, context_upgrade;
    private static MethodsAll instance;
    private CountDownTimer countDownTimer, countDownTimer_upgrade;
    private static long time_show = 5*60*1000; //minutes
    private SharedPreferences sharedPreferences;
    private int time_to_show_upgrade = 3;
    private int time_show_ads = 0;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setContext_upgrade(Context context_upgrade) {
        this.context_upgrade = context_upgrade;
        if(!new SharedPref(this.context_upgrade).getIsPremium()) {
            if (time_show_ads == time_to_show_upgrade) {
                time_show_ads = 0;
                showUpgradeDialog();
            } else {
            }
        }
    }

    private MethodsAll(){
        countDownTimer = new CountDownTimer(time_show,
                time_show) {
            @Override
            public void onTick(long l) {
                sharedPreferences = context.getSharedPreferences("save_time_use", Context.MODE_PRIVATE);
                if(!sharedPreferences.getBoolean("rated", false)) {
                    MethodsAll.getInstance().showRateDialog();
                }
            }
            @Override
            public void onFinish() {
                if(sharedPreferences.getBoolean("rated", false)){
                } else {
                    this.start();
                }
            }
        };
    }

    public static MethodsAll getInstance(){
        if(instance == null){
            instance = new MethodsAll();
        }
        return instance;
    }

    public void startCountdown_rate(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                countDownTimer.start();
            }
        }, time_show);
    }

    public void count_show_inter_ads(){
        time_show_ads += 1;
    }

    private void showUpgradeDialog(){
        if(context_upgrade != null) {
            if (dialog_upgrade != null) {
                if (dialog_upgrade.isShowing())
                    dialog_upgrade.dismiss();
            }
            dialog_upgrade = new Dialog(context_upgrade);
            dialog_upgrade.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog_upgrade.setContentView(R.layout.layout_dialog_rate);
            Window window = dialog_upgrade.getWindow();
            if (window == null) {
                return;
            }
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            WindowManager.LayoutParams windowAttributes = window.getAttributes();
            windowAttributes.gravity = Gravity.CENTER;
            dialog_upgrade.setCancelable(true);
            RatingBar ratingBar = dialog_upgrade.findViewById(R.id.ratingBar);
            ratingBar.setVisibility(View.GONE);
            TextView txt_dialog_rate = dialog_upgrade.findViewById(R.id.txt_dialog_rate);
            txt_dialog_rate.setText("Do you want to experience our app without ads?");
            Button btn_cancel_dialog = dialog_upgrade.findViewById(R.id.btn_cancel_dialog);
            btn_cancel_dialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog_upgrade.dismiss();
                }
            });

            Button btn_change_dialog = dialog_upgrade.findViewById(R.id.btn_change_dialog);
            btn_change_dialog.setText("Upgrade");
            btn_change_dialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context_upgrade.startActivity(new Intent(context, PurchaseActivity.class));
                    dialog_upgrade.dismiss();
                }
            });
            dialog_upgrade.show();
        }
    }

    private void showRateDialog(){
        if(context != null) {
            if (dialog != null) {
                if (dialog.isShowing())
                    dialog.dismiss();
            }
            dialog = new Dialog(context);
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
                }
            });

            Button btn_change_dialog = dialog.findViewById(R.id.btn_change_dialog);
            btn_change_dialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    int rating = (int) ratingBar.getRating();
                    editor.putBoolean("rated", true);
                    editor.commit();
                    if (rating >= 3) {
                        rateApp();
                    } else {
                    }
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    private void rateApp()
    {
        try
        {
            Intent rateIntent = rateIntentForUrl("market://details");
            context.startActivity(rateIntent);
        }
        catch (ActivityNotFoundException e)
        {
            Intent rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details");
            context.startActivity(rateIntent);
        }
    }

    private Intent rateIntentForUrl(String url)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, context.getPackageName())));
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
