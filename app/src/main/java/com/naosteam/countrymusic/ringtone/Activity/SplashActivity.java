package com.naosteam.countrymusic.ringtone.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.naosteam.countrymusic.BuildConfig;
import com.naosteam.countrymusic.R;
import com.naosteam.countrymusic.ringtone.Login.ItemUser;
import com.naosteam.countrymusic.ringtone.Login.LoadLogin;
import com.naosteam.countrymusic.ringtone.Login.LoginActivity;
import com.naosteam.countrymusic.ringtone.Login.LoginListener;
import com.naosteam.countrymusic.ringtone.Method.Methods;
import com.naosteam.countrymusic.ringtone.Receiver.LoadNemosofts;
import com.naosteam.countrymusic.ringtone.Receiver.NemosoftsListener;
import com.naosteam.countrymusic.ringtone.SharedPref.Setting;
import com.naosteam.countrymusic.ringtone.SharedPref.SharedPref;
import com.naosteam.countrymusic.ringtone.asyncTask.LoadAbout;
import com.naosteam.countrymusic.ringtone.interfaces.AboutListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {


    Methods methods;
    private static int SPLASH_TIME_OUT = 2000;


    SharedPref sharedPref;
    private static final String LOG_TAG = "iabv3";
    // put your Google merchant id here (as stated in public profile of your Payments Merchant Center)
    // if filled library will provide protection against Freedom alike Play Market simulators
    private static final String MERCHANT_ID=null;
    private BillingProcessor bp;
    private boolean readyToPurchase = false;

//    ServiceConnection mServiceConn = new ServiceConnection() {
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            mService = null;
//        }
//
//        @Override
//        public void onServiceConnected(ComponentName name,
//                                       IBinder service) {
//            mService = IInAppBillingService.Stub.asInterface(service);
//
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_ringtone);methods = new Methods(this);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        changeStatusBarColor();

        if (methods.isNetworkAvailable()) {
            //loadAboutData();
        } else {
            IntActivity();
        }

    }





    public void loadAboutData() {
        Toast.makeText(SplashActivity.this, "load About Data", Toast.LENGTH_SHORT).show();
        if (methods.isNetworkAvailable()) {
            LoadAbout loadAbout = new LoadAbout(SplashActivity.this, new AboutListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onEnd(String success, String verifyStatus, String message) {
                    if (success.equals("1")) {
                        if (!verifyStatus.equals("-1")) {
                            initBuy();
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


    public void Loadnemosofts() {
        if (sharedPref.getIsFirstPurchaseCode()) {
            Toast.makeText(SplashActivity.this, "load Settings", Toast.LENGTH_SHORT).show();
            LoadNemosofts loadAbout = new LoadNemosofts(SplashActivity.this, new NemosoftsListener() {
                @Override
                public void onStart() {
                }
                @Override
                public void onEnd(String success, String verifyStatus, String message) {
                    if (success.equals("1")) {
                        if (!verifyStatus.equals("-1")) {
                            if (BuildConfig.APPLICATION_ID.equals(Setting.itemAbout.getPackage_name())) {
                                sharedPref.setIsFirstPurchaseCode(false);
                                sharedPref.setPurchaseCode(Setting.itemAbout);
                                loadSettings();
                            } else {
                                errorDialog(getString(R.string.error_nemosofts_key_ringtone), getString(R.string.create_nemosofts_key_ringtone));
                            }
                        } else {
                            errorDialog(getString(R.string.error_nemosofts_key_ringtone), message);
                        }
                    } else {
                        errorDialog(getString(R.string.err_internet_not_conn), getString(R.string.error_connect_net_tryagain));
                    }
                }
            });
            loadAbout.execute();
        } else {
            sharedPref.getPurchaseCode();
            loadSettings();
        }
    }

    public void loadSettings() {
        if (sharedPref.getIsFirst()) {
            openLoginActivity();
        } else {
            if (!sharedPref.getIsAutoLogin()) {
                thiva();
            } else {
                if (methods.isNetworkAvailable()) {
                    loadLogin();
                } else {
                    thiva();
                }
            }
        }
    }

    private void loadLogin() {
        if (methods.isNetworkAvailable()) {
            LoadLogin loadLogin = new LoadLogin(new LoginListener() {
                @Override
                public void onStart() {
                }
                @Override
                public void onEnd(String success, String loginSuccess, String message, String user_id, String user_name) {
                    if (success.equals("1")) {
                        if (loginSuccess.equals("1")) {
                            Setting.itemUser = new ItemUser(user_id, user_name, sharedPref.getEmail(), "");
                            Setting.isLogged = true;
                            thiva();
                        } else {
                            thiva();
                        }
                    } else {
                        thiva();
                    }
                }
            }, methods.getAPIRequest(Setting.METHOD_LOGIN, 0, "", "", "", "", "", "", "", "", "", sharedPref.getEmail(), sharedPref.getPassword(), "", "", "", "", null));
            loadLogin.execute();
        } else {
            Toast.makeText(SplashActivity.this, getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
        }
    }

    private void errorDialog(String title, String message) {
        final AlertDialog.Builder  alertDialog ;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Setting.Dark_Mode){
                alertDialog = new AlertDialog.Builder(SplashActivity.this, R.style.ThemeDialog2);
            }else {
                alertDialog = new AlertDialog.Builder(SplashActivity.this, R.style.ThemeDialog);
            }
        } else {
            alertDialog = new AlertDialog.Builder(SplashActivity.this, R.style.ThemeDialog);
        }

        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);

        if (title.equals(getString(R.string.err_internet_not_conn)) || title.equals(getString(R.string.server_error))) {
            alertDialog.setNegativeButton(getString(R.string.try_again), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //loadAboutData();
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

    private void openLoginActivity() {
        Intent intent;
        if (Setting.isLoginOn && sharedPref.getIsFirst()) {
            sharedPref.setIsFirst(false);
            intent = new Intent(SplashActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("from", "");
        } else {
            intent = new Intent(SplashActivity.this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }


    private void thiva() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent main = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(main);
                finish();
            }
        },SPLASH_TIME_OUT);
    }


    private void IntActivity() {
        Intent mainb = new Intent(SplashActivity.this, intActivity.class);
        startActivity(mainb);
        finish();
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void initBuy() {
        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        //bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);


        if(!BillingProcessor.isIabServiceAvailable(this)) {

        }

        bp = new BillingProcessor(this, Setting.MERCHANT_KEY, MERCHANT_ID, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
                updateTextViews();
            }
            @Override
            public void onBillingError(int errorCode, @Nullable Throwable error) {
                //Loadnemosofts();
            }
            @Override
            public void onBillingInitialized() {
                readyToPurchase = true;
                updateTextViews();
            }
            @Override
            public void onPurchaseHistoryRestored() {
                for(String sku : bp.listOwnedProducts())
                    Log.d(LOG_TAG, "Owned Managed Product: " + sku);
                for(String sku : bp.listOwnedSubscriptions())
                    Log.d(LOG_TAG, "Owned Subscription: " + sku);
                updateTextViews();
            }
        });
        bp.loadOwnedPurchasesFromGoogle();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            //unbindService(mServiceConn);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void updateTextViews() {
        bp.loadOwnedPurchasesFromGoogle();
        if(isSubscribe(Setting.SUBSCRIPTION_ID)){
            Setting.getPurchases = true;
            //Loadnemosofts();
        } else{
            Setting.getPurchases = false;
            //Loadnemosofts();
        }
    }

    public Bundle getPurchases(){
        if (!bp.isInitialized()) {
            return null;
        }
        try{
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Boolean isSubscribe(String SUBSCRIPTION_ID_CHECK){

        if (!bp.isSubscribed(Setting.SUBSCRIPTION_ID))
            return false;


        Bundle b =  getPurchases();
        if (b==null)
            return  false;
        if( b.getInt("RESPONSE_CODE") == 0){
            ArrayList<String> ownedSkus =
                    b.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
            ArrayList<String>  purchaseDataList =
                    b.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
            ArrayList<String>  signatureList =
                    b.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
            String continuationToken =
                    b.getString("INAPP_CONTINUATION_TOKEN");


            if(purchaseDataList == null){
                return  false;

            }
            if(purchaseDataList.size()==0){
                return  false;
            }
            for (int i = 0; i < purchaseDataList.size(); ++i) {
                String purchaseData = purchaseDataList.get(i);
                String signature = signatureList.get(i);
                String sku_1 = ownedSkus.get(i);

                try {
                    JSONObject rowOne = new JSONObject(purchaseData);
                    String  productId =  rowOne.getString("productId") ;

                    if (productId.equals(SUBSCRIPTION_ID_CHECK)){

                        Boolean  autoRenewing =  rowOne.getBoolean("autoRenewing");
                        if (autoRenewing){
                            Long tsLong = System.currentTimeMillis()/1000;
                            Long  purchaseTime =  rowOne.getLong("purchaseTime")/1000;
                            return  true;
                        }else{
                            // Toast.makeText(this, "is not autoRenewing ", Toast.LENGTH_SHORT).show();
                            Long tsLong = System.currentTimeMillis()/1000;
                            Long  purchaseTime =  rowOne.getLong("purchaseTime")/1000;
                            if (tsLong > (purchaseTime + (Setting.SUBSCRIPTION_DURATION*86400)) ){
                                //   Toast.makeText(this, "is Expired ", Toast.LENGTH_SHORT).show();
                                return  false;
                            }else{
                                return  true;
                            }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }else{
            return false;
        }

        return  false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }


}