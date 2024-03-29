package com.naosteam.countrymusic.ringtone.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.naosteam.countrymusic.HomeActivity;
import com.naosteam.countrymusic.MethodsAll;
import com.naosteam.countrymusic.R;
import com.naosteam.countrymusic.mp3.interfaces.InterScreenListener;
import com.naosteam.countrymusic.mp3.utils.Constant;
import com.naosteam.countrymusic.mp3.utils.SharedPref;
import com.naosteam.countrymusic.radio.item.ItemRadio;
import com.naosteam.countrymusic.radio.utils.Constants;
import com.naosteam.countrymusic.ringtone.Adapter.CategoryAdapter;
import com.naosteam.countrymusic.ringtone.Method.Methods;
import com.naosteam.countrymusic.ringtone.SharedPref.Setting;
import com.naosteam.countrymusic.ringtone.asyncTask.LoadCat;
import com.naosteam.countrymusic.ringtone.interfaces.CatListener;
import com.naosteam.countrymusic.ringtone.item.ListltemCategory;

import java.util.ArrayList;

public class BaseCategoriesActivity extends AppCompatActivity {

    public View rootView;
    private Methods methods;
    private RecyclerView recyclerView_category;
    private CategoryAdapter adapterCat;
    private ArrayList<Object> arrayList;
    private int page = 0;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private com.naosteam.countrymusic.mp3.utils.Methods methods1;
    private LinearLayout adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_categories);

        methods = new Methods(BaseCategoriesActivity.this);
        arrayList = new ArrayList<>();
        progressBar = findViewById(R.id.progressbar);

        MethodsAll.getInstance().setContext(BaseCategoriesActivity.this);
        MethodsAll.getInstance().setContext_upgrade(BaseCategoriesActivity.this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Categories");

        methods1 = new com.naosteam.countrymusic.mp3.utils.Methods(this);
        adView = findViewById(R.id.adView);
        methods1.showSMARTBannerAd(adView);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methods1.showInterScreenAd(new InterScreenListener() {
                    @Override
                    public void onClick() {
                        finish();
                    }
                });

            }
        });

        recyclerView_category = findViewById(R.id.category);
        GridLayoutManager mLayoutManager = new GridLayoutManager(BaseCategoriesActivity.this, 2);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (arrayList.get(position) instanceof AdView) {
                    return 2;
                } else {
                    return 1;
                }
            }
        });

        recyclerView_category.setLayoutManager(mLayoutManager);
        recyclerView_category.addItemDecoration(new BaseCategoriesActivity.GridSpacingItemDecoration(2, dpToPx(2), true));
        recyclerView_category.setItemAnimator(new DefaultItemAnimator());


        loadCategories();
    }

    private void loadCategories() {
        if (methods.isNetworkAvailable()) {
            LoadCat loadCat = new LoadCat(new CatListener() {
                @Override
                public void onStart() {
                    progressBar.setVisibility(View.VISIBLE);
                    if (arrayList.size() != 0) {
                        arrayList.clear();
                    }
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ListltemCategory> arrayListCat) {
                    if (BaseCategoriesActivity.this != null) {
                        if (success.equals("1")) {
                            if (!verifyStatus.equals("-1")) {
                                if (arrayListCat.size() == 0) {

                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    page = page + 1;
                                    arrayList.addAll(arrayListCat);
                                    getBannerAds();
                                    setAdapter();
                                }
                            } else {
                                methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                            }
                        } else {

                        }
                    }
                }
            }, methods.getAPIRequest(Setting.METHOD_CAT, page, "", "", "", "", "", "", "", "","","","","","","","", null));
            loadCat.execute();
        } else {

        }
    }

    private void getBannerAds(){

        SharedPref mp3_pref = new SharedPref(this);
        if(mp3_pref.getIsPremium()){
            return;
        }

        for (int i = 4; i < arrayList.size(); i += 4+1){
            if(arrayList.get(i) instanceof ListltemCategory){
                if(Constants.adBannerShow++ < Constants.BANNER_SHOW_LIMIT){
                    final AdView adView = new AdView(this);
                    adView.setAdSize(AdSize.SMART_BANNER);
                    adView.setAdUnitId(Constant.ad_banner_id_test);
                    adView.loadAd(new AdRequest.Builder().build());
                    arrayList.add(i, adView);
                }
            }
        }
    }

    private void setAdapter() {
        adapterCat = new CategoryAdapter(arrayList, BaseCategoriesActivity.this);
        recyclerView_category.setAdapter(adapterCat);
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    protected void onResume() {
        MethodsAll.getInstance().setContext(BaseCategoriesActivity.this);
        MethodsAll.getInstance().setContext_upgrade(BaseCategoriesActivity.this);
        super.onResume();
    }
}