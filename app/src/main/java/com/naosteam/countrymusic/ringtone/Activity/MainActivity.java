package com.naosteam.countrymusic.ringtone.Activity;

import static android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.naosteam.countrymusic.MethodsAll;
import com.naosteam.countrymusic.PurchaseActivity;
import com.naosteam.countrymusic.R;
import com.naosteam.countrymusic.mp3.interfaces.InterScreenListener;
import com.naosteam.countrymusic.ringtone.Adapter.ViewPagerAdapter;
import com.naosteam.countrymusic.ringtone.Constant.Constant;
import com.naosteam.countrymusic.ringtone.DBHelper.DBHelper;
import com.naosteam.countrymusic.ringtone.Method.Methods;
import com.naosteam.countrymusic.ringtone.SharedPref.Setting;
import com.naosteam.countrymusic.ringtone.SharedPref.SharedPref;

import java.io.File;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private ViewPager viewPager;
    private TabLayout tabLayout;

    DrawerLayout drawer;
    Toolbar toolbar;

    NavigationView navigationView;

    MenuItem menu_login, menu_upload,menu_user_by, menu_fav;
    public static Activity activity;
    Methods methods;
    DBHelper dbHelper;
    com.naosteam.countrymusic.mp3.utils.Methods methods1;
    LinearLayout adView;

    SharedPref sharedPref;

    // put your Google merchant id here (as stated in public profile of your Payments Merchant Center)
    // if filled library will provide protection against Freedom alike Play Market simulators
    private Menu menu;
    private static final String MERCHANT_ID=null;
    private  Boolean DialogOpened = false;
    private TextView text_view_go_pro;
    private BillingProcessor bp;
    private boolean readyToPurchase = false;
    private static final String LOG_TAG = "iabv3";

//    UpdateManager update;
//    Nemosofts nemosofts;

//    ServiceConnection mServiceConn = new ServiceConnection() {
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            mService = null;
//            // Toast.makeText(MainActivity.this, "set null", Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onServiceConnected(ComponentName name,
//                                       IBinder service) {
//            mService = IInAppBillingService.Stub.asInterface(service);
//            //Toast.makeText(MainActivity.this, "set Stub", Toast.LENGTH_SHORT).show();
//
//        }
//    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        if (Setting.Dark_Mode) {
//            setTheme(R.style.AppTheme);
//        } else {
//            setTheme(R.style.AppTheme);
//        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ringtone);

        MethodsAll.getInstance().setContext(MainActivity.this);
        //initBuy();
//        nemosofts = new Nemosofts(this);

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/poppins_reg.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());
        dbHelper = new DBHelper(this);
        sharedPref = new SharedPref(MainActivity.this);

        methods1 = new com.naosteam.countrymusic.mp3.utils.Methods(this);
        adView = findViewById(R.id.adView);
        methods1.showSMARTBannerAd(adView);
//        nemosofts = new Nemosofts(this);
//        // Initialize the Update Manager with the Activity and the Update Mode
//        update = UpdateManager.Builder(this).mode(UpdateManagerConstant.FLEXIBLE);
//        update.start();
//
//        update.getAvailableVersionCode(new UpdateManager.onVersionCheckListener() {
//            @Override
//            public void onReceiveVersionCode(final int code) {
//                // Do something here
//            }
//        });


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Home");

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        toggle.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        toggle.setDrawerIndicatorEnabled(false);


        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();
        menu_login = menu.findItem(R.id.nav_login);
        menu_upload = menu.findItem(R.id.nav_upload);
        menu_user_by = menu.findItem(R.id.nav_user_by);
        menu_fav = menu.findItem(R.id.nav_fav);
        changeLoginName();


        viewPager = (ViewPager) findViewById(R.id.view_pager);

        String[] pageTitle = {"All", "Top 10 Views"};
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        for (int i = 0; i < 2; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(pageTitle[i]));
        }

        //set gravity for tab bar
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(2);

        //change Tab selection when swipe ViewPager
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        //change ViewPager page when tab selected
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //methods.showBannerAd(adView);

        checkIfAlreadyhavePermission(this);
        requestForSpecificPermission(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            openAccessFileDialog();
        }
    }

    private void openAccessFileDialog() {

        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);

        dialog.setContentView(R.layout.dialog_alert);

        TextView maintext = dialog.findViewById(R.id.maintext);
        maintext.setText("Please give permission to this app to access all file!");

        Button img_btn_yes = dialog.findViewById(R.id.yes);
        Button img_btn_no = dialog.findViewById(R.id.no);

        img_btn_no.setOnClickListener(v ->{
            dialog.dismiss();
            quitScreen();
        });

        img_btn_yes.setOnClickListener(v->{
            Intent i = new Intent();
            i.setAction(ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            startActivityIfNeeded(i, 11);
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 11 && resultCode == RESULT_OK){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()){
                Toast.makeText(this, "Grant permission successfully!", Toast.LENGTH_SHORT).show();
            }else{
                quitScreen();
            }
        }
    }

    private void quitScreen(){
        Toast.makeText(this, "Please allow the permissions to continue", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 1500);
    }

    private void changeLoginName() {
        if (menu_login != null) {
            if (Setting.isLoginOn) {
                if (Setting.isLogged) {
                    //menu_login.setTitle(getResources().getString(R.string.logout));
                   // menu_login.setIcon(getResources().getDrawable(R.drawable.ic_logout));
                    menu_upload.setVisible(false);
                    menu_user_by.setVisible(false);
                    menu_fav.setVisible(true);
                } else {
                    //menu_login.setTitle(getResources().getString(R.string.login));
                    //menu_login.setIcon(getResources().getDrawable(R.drawable.ic_login));
                    menu_upload.setVisible(false);
                    menu_user_by.setVisible(false);
                    menu_fav.setVisible(false);
                }
            } else {
                menu_login.setVisible(false);
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_home:
                break;
            case R.id.nav_user_by:
                methods1.showInterScreenAd(new InterScreenListener() {
                    @Override
                    public void onClick() {
                        methods1.showInterScreenAd(new InterScreenListener() {
                            @Override
                            public void onClick() {
                                Stop();
                                Intent intent_user = new Intent(MainActivity.this, UserActivity.class);
                                startActivity(intent_user);
                            }
                        });
                    }
                });
                break;
            case R.id.nav_fav:
                methods1.showInterScreenAd(new InterScreenListener() {
                    @Override
                    public void onClick() {
                        Stop();
                        Intent intent_fav = new Intent(MainActivity.this, FavouriteActivity.class);
                        startActivity(intent_fav);
                    }
                });

                break;
            case R.id.nav_download:
                methods1.showInterScreenAd(new InterScreenListener() {
                    @Override
                    public void onClick() {
                        Stop();
                        Intent intent = new Intent(MainActivity.this, DownloadActivity.class);
                        startActivity(intent);
                    }
                });
                break;
            case R.id.nav_upload:
                methods1.showInterScreenAd(new InterScreenListener() {
                    @Override
                    public void onClick() {
                        Intent update = new Intent(MainActivity.this,UploadRingtoneActivity.class);
                        startActivity(update);
                    }
                });
                break;
//            case R.id.nav_set:
//                overridePendingTransition(0, 0);
//                overridePendingTransition(0, 0);
//                startActivity(new Intent(MainActivity.this, SettingActivity.class));
//                finish();
//                break;
//            case R.id.nav_login:
//                methods.clickLogin();
//                break;
            case R.id.nav_back:
                methods1.showInterScreenAd(new InterScreenListener() {
                    @Override
                    public void onClick() {
                        finish();
                    }
                });

//                Stop();
//                Intent back = new Intent(MainActivity.this, HomeActivity.class);
//                startActivity(back);
                break;

            case R.id.nav_cat:
                methods1.showInterScreenAd(new InterScreenListener() {
                    @Override
                    public void onClick() {
                        Stop();
                        Intent cat = new Intent(MainActivity.this, BaseCategoriesActivity.class);
                        startActivity(cat);
                    }
                });

                break;

            case R.id.nav_search:
                methods1.showInterScreenAd(new InterScreenListener() {
                    @Override
                    public void onClick() {
                        Stop();
                        Intent search = new Intent(MainActivity.this, SearchActivity.class);
                        startActivity(search);
                    }
                });

            case R.id.nav_premium:
                methods1.showInterScreenAd(new InterScreenListener() {
                    @Override
                    public void onClick() {
                        Stop();
                        Intent pre = new Intent(MainActivity.this, PurchaseActivity.class);
                        startActivity(pre);
                    }
                });

                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }





    public static boolean checkIfAlreadyhavePermission(Activity context) {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            File dir = new File(Environment.getExternalStorageDirectory() + File.separator + Constant.appFolderName);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            return true;
        } else {
            return false;
        }
    }

    public static void requestForSpecificPermission(Activity context) {

        ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 101);


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    File dir = new File(Environment.getExternalStorageDirectory() + File.separator + Constant.appFolderName);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }

                } else {
                    boolean showRationale = false;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        showRationale = shouldShowRequestPermissionRationale( Manifest.permission.WRITE_EXTERNAL_STORAGE );
                    }
                    if (! showRationale) {
                        if (sharedPref.getCheckPermission()){
                            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                            alertDialogBuilder.setTitle("Permissions Required")
                                    .setMessage("You have forcefully denied some of the required permissions " +
                                            "for this action. Please open settings, go to permissions and allow them.")
                                    .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                    Uri.fromParts("package", getPackageName(), null));
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .setCancelable(false)
                                    .create()
                                    .show();
                        }
                        sharedPref.setCheckPermission(false);

                    } else if (Manifest.permission.WRITE_CONTACTS.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        Toast.makeText(activity, "We need permissions to manage songs", Toast.LENGTH_SHORT).show();
                    }

                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private void Stop() {
        try {
            Setting.exoPlayer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        try {
            Setting.exoPlayer.stop();
            Setting.exoPlayer.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        try {
            Setting.exoPlayer.stop();
            Setting.exoPlayer.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_ringtone, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
//            case R.id.action_pro :
//                showDialog_pay();
//
//                break;

            case R.id.item_search :
                methods1.showInterScreenAd(new InterScreenListener() {
                    @Override
                    public void onClick() {
                        startActivity(new Intent(MainActivity.this, SearchActivity.class));
                        Stop();
                    }
                });

                break;
            case R.id.item_purchase:
                methods1.showInterScreenAd(new InterScreenListener() {
                    @Override
                    public void onClick() {

                        startActivity(new Intent(MainActivity.this, PurchaseActivity.class));
                        Stop();
                    }
                });

                break;



        }
        return super.onOptionsItemSelected(item);
    }


    private void initBuy() {
        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        //bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);


        if(!BillingProcessor.isIabServiceAvailable(this)) {
            //  showToast("In-app billing service is unavailable, please upgrade Android Market/Play to version >= 3.9.16");
        }

        bp = new BillingProcessor(this, Setting.MERCHANT_KEY, MERCHANT_ID, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
                //  showToast("onProductPurchased: " + productId);
                Intent intent= new Intent(MainActivity.this,SplashActivity.class);
                startActivity(intent);
                finish();
                updateTextViews();
            }
            @Override
            public void onBillingError(int errorCode, @Nullable Throwable error) {
                // showToast("onBillingError: " + Integer.toString(errorCode));
            }
            @Override
            public void onBillingInitialized() {
                //  showToast("onBillingInitialized");
                readyToPurchase = true;
                updateTextViews();
            }
            @Override
            public void onPurchaseHistoryRestored() {
                // showToast("onPurchaseHistoryRestored");
                for(String sku : bp.listOwnedProducts())
                    Log.d(LOG_TAG, "Owned Managed Product: " + sku);
                for(String sku : bp.listOwnedSubscriptions())
                    Log.d(LOG_TAG, "Owned Subscription: " + sku);
                updateTextViews();
            }
        });
        bp.loadOwnedPurchasesFromGoogle();
    }
    private void updateTextViews() {
        bp.loadOwnedPurchasesFromGoogle();
    }

    public Bundle getPurchases(){
        if (!bp.isInitialized()) {


            //  Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
            return null;
        }
        try{
            // Toast.makeText(this, "good", Toast.LENGTH_SHORT).show();
        }catch (Exception e) {
            //  Toast.makeText(this, "ex", Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }
        return null;
    }


    public void showDialog_pay(){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.dialog_subscribe_ringtone, null);

        final BottomSheetDialog dialog_setas = new BottomSheetDialog(this);
        dialog_setas.setContentView(view);
        //dialog_setas.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);

        this.text_view_go_pro=(TextView) dialog_setas.findViewById(R.id.text_view_go_pro);
        RelativeLayout relativeLayout_close_rate_gialog=(RelativeLayout) dialog_setas.findViewById(R.id.relativeLayout_close_rate_gialog);
        relativeLayout_close_rate_gialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_setas.dismiss();
            }
        });
        text_view_go_pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bp.subscribe(MainActivity.this, Setting.SUBSCRIPTION_ID);
            }
        });
        dialog_setas.setOnKeyListener(new BottomSheetDialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    dialog_setas.dismiss();
                }
                return true;
            }
        });
        dialog_setas.show();
        DialogOpened=true;

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Continue updates when resumed
//        update.continueUpdate();
    }

}
