package com.zxfdwka.bestcountrymusic.ringtone.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.nemosofts.library.SwitchButton.SwitchButton;
import com.zxfdwka.bestcountrymusic.BuildConfig;
import com.zxfdwka.bestcountrymusic.R;
import com.zxfdwka.bestcountrymusic.ringtone.Constant.Constant;
import com.zxfdwka.bestcountrymusic.ringtone.Method.Methods;
import com.zxfdwka.bestcountrymusic.ringtone.SharedPref.Setting;
import com.zxfdwka.bestcountrymusic.ringtone.SharedPref.SharedPref;


public class SettingActivity extends AppCompatActivity {
    SharedPref sharedPref;
    SwitchButton switch_dark;
    LinearLayout about, share,privacy;
    TextView version;
    Methods methods;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.getNightMode()) {
            setTheme(R.style.AppTheme);
            Setting.Dark_Mode = true;
        } else {
            setTheme(R.style.AppTheme);
            Setting.Dark_Mode = false;
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_ringtone);

        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());

        Toolbar toolbar = findViewById(R.id.toolbar_setting);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getResources().getString(R.string.settings));

        this.setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Dark_mode();

        version = (TextView) findViewById(R.id.version);
        version.setText(BuildConfig.VERSION_NAME);

        about= findViewById(R.id.nav_about);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent player = new Intent(SettingActivity.this,AboutActivity.class);
                startActivity(player);
            }
        });

        share  = findViewById(R.id.nav_share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String appName = getPackageName();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
            }
        });

        privacy  = findViewById(R.id.nav_privacy);
        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOpnsDialog();
            }
        });



    }



    public void openOpnsDialog() {
        Dialog dialog;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog = new Dialog(SettingActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            dialog = new Dialog(SettingActivity.this);
        }

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_privacy_ringtone);

        WebView webview = dialog.findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);

        webview.loadUrl(Constant.privacy_policy_url);

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }



    private void Dark_mode() {
        switch_dark = findViewById(R.id.switch_dark);
        if (sharedPref.getNightMode()) {
            switch_dark.setChecked(true);
        } else {
            switch_dark.setChecked(false);
        }
        switch_dark.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                sharedPref.setNightMode(isChecked);
                Apps_recreate();
            }
        });
    }



    private void Apps_recreate() {
        recreate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                overridePendingTransition(0, 0);
                overridePendingTransition(0, 0);
                startActivity(new Intent(SettingActivity.this, MainActivity.class));
                finish();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(0, 0);
        overridePendingTransition(0, 0);
        startActivity(new Intent(SettingActivity.this, MainActivity.class));
        finish();
    }
}