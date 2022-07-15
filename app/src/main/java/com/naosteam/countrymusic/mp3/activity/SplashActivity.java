package com.naosteam.countrymusic.mp3.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.QueryPurchasesParams;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.naosteam.countrymusic.HomeActivity;
import com.naosteam.countrymusic.R;
import com.naosteam.countrymusic.mp3.asyncTask.LoadAbout;
import com.naosteam.countrymusic.mp3.asyncTask.LoadLogin;
import com.naosteam.countrymusic.mp3.interfaces.AboutListener;
import com.naosteam.countrymusic.mp3.interfaces.LoginListener;
import com.naosteam.countrymusic.mp3.item.ItemUser;
import com.naosteam.countrymusic.mp3.utils.Constant;
import com.naosteam.countrymusic.mp3.utils.DBHelper;
import com.naosteam.countrymusic.mp3.utils.Methods;
import com.naosteam.countrymusic.mp3.utils.SharedPref;
import com.facebook.AccessToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    SharedPref sharedPref;
    Methods methods;
    DBHelper dbHelper;
    ProgressBar pb_latest;
    BillingClient billingClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        hideStatusBar();
        methods = new Methods(this);
        sharedPref = new SharedPref(this);
        dbHelper = new DBHelper(this);
        pb_latest = findViewById(R.id.pb_latest);
        Sprite doubleBounce = new DoubleBounce();
        doubleBounce.setColor(R.color.colorPrimary);
        pb_latest.setIndeterminateDrawable(doubleBounce);

        if (sharedPref.getIsFirst()) {
            loadAboutData();
        } else {

            loadAboutData();

            try {
                Constant.isFromPush = getIntent().getExtras().getBoolean("ispushnoti", false);
            } catch (Exception e) {
                Constant.isFromPush = false;
            }
            try {
                Constant.isFromNoti = getIntent().getExtras().getBoolean("isnoti", false);
            } catch (Exception e) {
                Constant.isFromNoti = false;
            }

            checkUserSubscription();

            if (!sharedPref.getIsAutoLogin()) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        openMainActivity();
                    }
                }, 2000);
            } else {
                if (sharedPref.getLoginType().equals(Constant.LOGIN_TYPE_FB)) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        loadLogin(Constant.LOGIN_TYPE_FB, sharedPref.getAuthID());
                    } else {
                        sharedPref.setIsAutoLogin(false);
                        openMainActivity();
                    }
                } else if (sharedPref.getLoginType().equals(Constant.LOGIN_TYPE_GOOGLE)) {
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (currentUser != null) {
                        loadLogin(Constant.LOGIN_TYPE_GOOGLE, sharedPref.getAuthID());
                    } else {
                        sharedPref.setIsAutoLogin(false);
                        openMainActivity();
                    }
                } else {
                    loadLogin(Constant.LOGIN_TYPE_NORMAL, "");
                }
            }
        }
    }

    private void checkUserSubscription() {
        billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener((billingResult, list) -> {}).build();
        final BillingClient finalBillingClient = billingClient;
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {

            }

            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {

                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                    finalBillingClient.queryPurchasesAsync(
                            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build(),
                            (billingResult1, purchases) -> {
                                if (billingResult1.getResponseCode() == BillingClient.BillingResponseCode.OK){

                                    boolean isPremium = false;

                                    for(Purchase purchase : purchases){
                                        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && purchase.isAcknowledged()) {
                                            isPremium = true;
                                        }
                                    }

                                    sharedPref.setIsPremium(isPremium);

                                }else{
                                    sharedPref.setIsPremium(false);
                                }
                            });

                }

            }
        });
    }

    private void loadLogin(final String loginType, final String authID) {
        if (methods.isNetworkAvailable()) {
            LoadLogin loadLogin = new LoadLogin(new LoginListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onEnd(String success, String loginSuccess, String message, String user_id, String user_name) {

                    if (success.equals("1")) {
                        if (loginSuccess.equals("1")) {
                            Constant.itemUser = new ItemUser(user_id, user_name, sharedPref.getEmail(), "",authID, loginType);

                            Constant.isLogged = true;
                        } else {

                        }
                        openMainActivity();
                    } else {
                        openMainActivity();
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_LOGIN, 0, authID, "", "", loginType, "", "", "", "", "", sharedPref.getEmail(), sharedPref.getPassword(), "", "", "", "", null));
            loadLogin.execute();
        } else {
            Toast.makeText(SplashActivity.this, getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
        }
    }

    public void loadAboutData() {
        if (methods.isNetworkAvailable()) {
            LoadAbout loadAbout = new LoadAbout(SplashActivity.this, new AboutListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onEnd(String success, String verifyStatus, String message) {
                    if (success.equals("1")) {
                        if (!verifyStatus.equals("-1") && !verifyStatus.equals("-2")) {
                            String version = "";
                            try {
                                PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                                version = String.valueOf(pInfo.versionCode);
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                            if(Constant.showUpdateDialog && !Constant.appVersion.equals(version)) {
                                methods.showUpdateAlert(Constant.appUpdateMsg);
                            } else {
                                sharedPref.setIsFirst(false);
                                dbHelper.addtoAbout();
                                openMainActivity();
                            }
                        } else if (verifyStatus.equals("-2")) {
                            methods.getInvalidUserDialog(message);
                        } else {
                            errorDialog(getString(R.string.error_unauth_access), message);
                        }
                    } else {
                        errorDialog(getString(R.string.server_error), getString(R.string.err_server));
                    }
                }
            });
            loadAbout.execute();
        } else {
            errorDialog(getString(R.string.err_internet_not_conn), getString(R.string.error_connect_net_tryagain));
        }
    }

    private void errorDialog(String title, String message) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplashActivity.this, R.style.ThemeDialog);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);

        if (title.equals(getString(R.string.err_internet_not_conn)) || title.equals(getString(R.string.server_error))) {
            alertDialog.setNegativeButton(getString(R.string.try_again), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    loadAboutData();
                }
            });
        }

        alertDialog.setPositiveButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertDialog.show();
    }

//    private void openLoginActivity() {
//        Intent intent;
//        if (Constant.isLoginOn && sharedPref.getIsFirst()) {
//            sharedPref.setIsFirst(false);
//            intent = new Intent(SplashActivity.this, LoginActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            intent.putExtra("from", "");
//        } else {
//            intent = new Intent(SplashActivity.this, MainActivity.class);
//        }
//        startActivity(intent);
//        finish();
//    }

    private void openMainActivity() {
//        Intent intent;
//        if (Constant.isFromPush && !Constant.pushCID.equals("0")) {
//            intent = new Intent(SplashActivity.this, SongByCatActivity.class);
//            intent.putExtra("isPush", true);
//            intent.putExtra("type", getString(R.string.categories));
//            intent.putExtra("id", Constant.pushCID);
//            intent.putExtra("name", Constant.pushCName);
//            intent.putExtra("image", "");
//        } else if (Constant.isFromPush && !Constant.pushArtID.equals("0")) {
//            intent = new Intent(SplashActivity.this, SongByCatActivity.class);
//            intent.putExtra("isPush", true);
//            intent.putExtra("type", getString(R.string.artist));
//            intent.putExtra("id", Constant.pushArtID);
//            intent.putExtra("name", Constant.pushArtNAME);
//            intent.putExtra("image", "");
//        } else if (Constant.isFromPush && !Constant.pushAlbID.equals("0")) {
//            intent = new Intent(SplashActivity.this, SongByCatActivity.class);
//            intent.putExtra("isPush", true);
//            intent.putExtra("type", getString(R.string.albums));
//            intent.putExtra("id", Constant.pushAlbID);
//            intent.putExtra("name", Constant.pushAlbNAME);
//            intent.putExtra("image", "");
//        } else {
//            intent = new Intent(SplashActivity.this, MainActivity.class);
//        }
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                finish();
            }
        },2000);

    }

    void hideStatusBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
}