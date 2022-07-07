package com.zxfdwka.bestcountrymusic.ringtone.Fragment;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zxfdwka.bestcountrymusic.R;
import com.zxfdwka.bestcountrymusic.ringtone.Adapter.CategoryAdapter;
import com.zxfdwka.bestcountrymusic.ringtone.Method.Methods;
import com.zxfdwka.bestcountrymusic.ringtone.SharedPref.Setting;
import com.zxfdwka.bestcountrymusic.ringtone.asyncTask.LoadCat;
import com.zxfdwka.bestcountrymusic.ringtone.interfaces.CatListener;
import com.zxfdwka.bestcountrymusic.ringtone.item.ListltemCategory;

import java.util.ArrayList;

public class CategoriesFragment extends Fragment {

    public View rootView;
    private Methods methods;
    private RecyclerView recyclerView_category;
    private CategoryAdapter adapterCat;
    private ArrayList<ListltemCategory> arrayList;
    private int page = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.categories_fragment_ringtone, container, false);

        methods = new Methods(getActivity());
        arrayList = new ArrayList<>();

        recyclerView_category = rootView.findViewById(R.id.category);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView_category.setLayoutManager(mLayoutManager);
        recyclerView_category.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(2), true));
        recyclerView_category.setItemAnimator(new DefaultItemAnimator());

        loadCategories();

        return rootView;
    }

    private void loadCategories() {
        if (methods.isNetworkAvailable()) {
            LoadCat loadCat = new LoadCat(new CatListener() {
                @Override
                public void onStart() {
                    if (arrayList.size() == 0) {
                        arrayList.clear();
                    }
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ListltemCategory> arrayListCat) {
                    if (getActivity() != null) {
                        if (success.equals("1")) {
                            if (!verifyStatus.equals("-1")) {
                                if (arrayListCat.size() == 0) {

                                } else {
                                    page = page + 1;
                                    arrayList.addAll(arrayListCat);
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

    private void setAdapter() {
        adapterCat = new CategoryAdapter(arrayList, getContext());
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
}