package com.naosteam.countrymusic.radio.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.naosteam.countrymusic.R;
import com.naosteam.countrymusic.radio.activity.RadioBaseActivity;
import com.naosteam.countrymusic.mp3.interfaces.InterAdListener;
import com.naosteam.countrymusic.radio.item.ItemRadio;
import com.naosteam.countrymusic.radio.utils.Constants;
import com.naosteam.countrymusic.radio.utils.DBHelper;
import com.naosteam.countrymusic.radio.utils.Methods;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class AdapterRadioList extends RecyclerView.Adapter {

    private DBHelper dbHelper;
    private ArrayList<Object> arrayList;
    private Context context;
    private ArrayList<Object> filteredArrayList;
    private NameFilter filter;
    private Methods methods;
    private com.naosteam.countrymusic.mp3.utils.Methods mp3_methods;
    private ConstraintLayout.LayoutParams lp_item;
    private boolean isGrid;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView textView_radio, textView_freq;
        private ConstraintLayout cs_item;

        private MyViewHolder(View view) {
            super(view);
            cs_item = view.findViewById(R.id.cs_item);
            textView_radio = view.findViewById(R.id.tv_cityhome_text);
            textView_freq = view.findViewById(R.id.tv_cityhome_text_city);
            imageView = view.findViewById(R.id.image_city_item);;
        }
    }

    public class BannerAdsViewHolder extends RecyclerView.ViewHolder{

        public BannerAdsViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }

    public AdapterRadioList(Context context, ArrayList<Object> arrayList, ConstraintLayout.LayoutParams lp_item, boolean isGrid) {
        this.arrayList = arrayList;
        this.context = context;
        this.lp_item = lp_item;
        methods = new Methods(context);
        mp3_methods = new com.naosteam.countrymusic.mp3.utils.Methods(context, interAdListener);
        filteredArrayList = arrayList;
        dbHelper = new DBHelper(context);
        this.isGrid = isGrid;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView;

        switch (viewType){
            case Constants.ITEM_BANNER_AD:
                View bannerAdView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.banner_ad_containter, parent, false);
                return new BannerAdsViewHolder(bannerAdView);
            case Constants.ITEM_RADIO_HOME_GRID:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_radiolist_grid, parent, false);

                return new MyViewHolder(itemView);
            default:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_radiolist, parent, false);

                return new MyViewHolder(itemView);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {

        int viewType = getItemViewType(position);
        switch (viewType){
            case Constants.ITEM_BANNER_AD:
                BannerAdsViewHolder adsViewHolder = (BannerAdsViewHolder) holder;
                AdView adView = (AdView) arrayList.get(position);
                ViewGroup adCardView = (ViewGroup) adsViewHolder.itemView;

                if(adCardView.getChildCount()>0){
                    adCardView.removeAllViews();
                }
                adCardView.addView(adView);
                break;
            default:
                ItemRadio objAllBean = (ItemRadio) arrayList.get(position);
                Boolean isFav = checkFav(position);

                MyViewHolder myViewHolder = (MyViewHolder) holder;

                myViewHolder.textView_radio.setText(objAllBean.getRadioName());
                myViewHolder.textView_freq.setText(objAllBean.getRadioFreq());

                DisplayMetrics displayMetrics = new DisplayMetrics();
                ((RadioBaseActivity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = displayMetrics.heightPixels;
                int width = displayMetrics.widthPixels;

                myViewHolder.textView_radio.setTypeface(null, Typeface.BOLD);
                myViewHolder.textView_radio.setTextSize(TypedValue.COMPLEX_UNIT_PX, height*0.02f);
                myViewHolder.textView_freq.setTextSize(TypedValue.COMPLEX_UNIT_PX, height*0.014f);

                Picasso.get()
                        .load(methods.getImageThumbSize(objAllBean.getRadioImageurl().replace(" ", "%20"),context.getString(R.string.home)))
                        .placeholder(R.drawable.placeholder)
                        .into(myViewHolder.imageView);

                myViewHolder.cs_item.setLayoutParams(lp_item);

                myViewHolder.cs_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mp3_methods.showInterAd(holder.getAdapterPosition(), "");
                    }
                });

                //                myViewHolder.imageView_fav.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if (dbHelper.addORremoveFav((ItemRadio) arrayList.get(holder.getAdapterPosition()))) {
//                            myViewHolder.imageView_fav.setImageResource(R.drawable.radio_fav);
//                            methods.showToast(context.getString(R.string.add_to_fav));
//                        } else {
//                            myViewHolder.imageView_fav.setImageResource(R.drawable.radio_unfav);
//                            methods.showToast(context.getString(R.string.remove_from_fav));
//                        }
//                    }
//                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if(arrayList.get(position) instanceof AdView){
            return Constants.ITEM_BANNER_AD;
        }else{
            if(isGrid){
                return Constants.ITEM_RADIO_HOME_GRID;
            }else{
                return Constants.ITEM_RADIO_HOME;
            }

        }
    }

    private Boolean checkFav(int pos) {
        return dbHelper.checkFav((ItemRadio) arrayList.get(pos));
    }

    public Filter getFilter() {
        if (filter == null) {
            filter = new NameFilter();
        }
        return filter;
    }

    private class NameFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint.toString().length() > 0) {
                ArrayList<ItemRadio> filteredItems = new ArrayList<>();

                for (int i = 0, l = filteredArrayList.size(); i < l; i++) {
                    Object obj = filteredArrayList.get(i);
                    if(obj instanceof ItemRadio){
                        ItemRadio item = (ItemRadio) obj;
                        String nameList = item.getRadioName();
                        if (nameList.toLowerCase().contains(constraint))
                            filteredItems.add(item);
                    }

                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            } else {
                synchronized (this) {
                    ArrayList<ItemRadio> filteredItems = new ArrayList<>();
                    for (Object obj:
                            filteredArrayList) {
                        if(obj instanceof ItemRadio){
                            ItemRadio item = (ItemRadio) obj;
                            filteredItems.add(item);
                        }
                    }
                    result.values = filteredItems;
                    result.count = filteredItems.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            arrayList = (ArrayList<Object>) results.values;
            notifyDataSetChanged();
        }
    }

    private InterAdListener interAdListener = new InterAdListener() {
        @Override
        public void onClick(int position, String type) {
            Constants.arrayList_radio.clear();
            for (Object o: arrayList){
                if(o instanceof ItemRadio){
                    Constants.arrayList_radio.add((ItemRadio) o);
                }
            }
            ((RadioBaseActivity) context).clickPlay(position, true);
        }
    };
}