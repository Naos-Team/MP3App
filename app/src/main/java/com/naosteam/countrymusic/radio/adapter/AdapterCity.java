package com.naosteam.countrymusic.radio.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.PaintDrawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.naosteam.countrymusic.R;
import com.naosteam.countrymusic.radio.activity.RadioBaseActivity;
import com.naosteam.countrymusic.radio.interfaces.CityClickListener;
import com.naosteam.countrymusic.mp3.interfaces.InterAdListener;
import com.naosteam.countrymusic.radio.item.ItemCity;
import com.naosteam.countrymusic.radio.utils.Constants;
import com.naosteam.countrymusic.mp3.utils.Methods;
import com.naosteam.countrymusic.radio.utils.SharedPref;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class AdapterCity extends RecyclerView.Adapter{

    private ArrayList<Object> arraylist;
    private ArrayList<Object> filteredArrayList;
    private NameFilter filter;
    private CityClickListener cityClickListener;
    private Methods methods;
    private SharedPref sharedPref;
    private ConstraintLayout.LayoutParams lp_item;
    private Context context;

    public AdapterCity(Context context, ArrayList<Object> list, CityClickListener cityClickListener, ConstraintLayout.LayoutParams lp_item) {
        this.arraylist = list;
        this.filteredArrayList = list;
        this.cityClickListener = cityClickListener;
        methods = new Methods(context, interAdListener);
        sharedPref = new SharedPref(context);
        this.lp_item = lp_item;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_title, tv_number;
        private RoundedImageView iv_demand;
        private ConstraintLayout cs_title, cs_item;

        private MyViewHolder(View view) {
            super(view);
            cs_title = view.findViewById(R.id.cs_title);
            cs_item = view.findViewById(R.id.cs_item);
            tv_title = view.findViewById(R.id.tv_title);
            tv_number = view.findViewById(R.id.tv_number);
            iv_demand = view.findViewById(R.id.iv_demand);
        }
    }
    public static class BannerAdsViewHolder extends RecyclerView.ViewHolder{

        public BannerAdsViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();

        switch (viewType){
            case Constants.ITEM_BANNER_AD:
                View bannerAdView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.banner_ad_containter, parent, false);
                return new BannerAdsViewHolder(bannerAdView);
            default:
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_ondemandcat2, parent, false);
                return new MyViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {

        int viewType = getItemViewType(position);

        switch (viewType){
            case Constants.ITEM_BANNER_AD:
                BannerAdsViewHolder adsViewHolder = (BannerAdsViewHolder) holder;
                AdView adView = (AdView) arraylist.get(position);
                ViewGroup adCardView = (ViewGroup) adsViewHolder.itemView;

                if(adCardView.getChildCount()>0){
                    adCardView.removeAllViews();
                }
                adCardView.addView(adView);
                break;
            default:
                MyViewHolder viewHolder = (MyViewHolder) holder;
                ItemCity item = (ItemCity) arraylist.get(position);

                String[] parts = item.getName().split("-");
                viewHolder.tv_number.setVisibility(View.GONE);
                viewHolder.tv_title.setText(parts[0].trim());

                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(viewHolder.cs_title);
                constraintSet.setVerticalBias(R.id.tv_title, 0.5f);
                constraintSet.applyTo(viewHolder.cs_title);

                viewHolder.cs_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        methods.showInterAd(holder.getAdapterPosition(), "");
                    }
                });

                DisplayMetrics displayMetrics = new DisplayMetrics();
                ((RadioBaseActivity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = displayMetrics.heightPixels;
                int width = displayMetrics.widthPixels;

                PaintDrawable paintDrawable = new PaintDrawable();
                paintDrawable.setCornerRadius(width*0.06f);
                paintDrawable.setTint(context.getResources().getColor(R.color.bg_radius_ondemand));
                viewHolder.cs_title.setBackground(paintDrawable);

                viewHolder.iv_demand.setCornerRadius(width*0.07f);

                viewHolder.tv_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, height*0.019f);
                viewHolder.tv_title.setMaxLines(2);
                viewHolder.tv_title.setTypeface(null, Typeface.BOLD);
                viewHolder.cs_item.setLayoutParams(lp_item);

                String url ="";

                switch (item.getTagLine()){
                    case "USA":
                        url = "https://cdn.countryflags.com/thumbs/united-states-of-america/flag-800.png";
                        break;
                    case "Germany":
                        url = "https://cdn.countryflags.com/thumbs/germany/flag-800.png";
                        break;
                    case "Italy":
                        url = "https://cdn.countryflags.com/thumbs/italy/flag-800.png";
                        break;
                    case "Australia":
                        url = "https://cdn.countryflags.com/thumbs/australia/flag-800.png";
                        break;
                    case "Austria":
                        url = "https://cdn.countryflags.com/thumbs/austria/flag-800.png";
                        break;
                    case "Sweden":
                        url = "https://cdn.countryflags.com/thumbs/sweden/flag-800.png";
                        break;
                    case "France":
                        url = "https://cdn.countryflags.com/thumbs/france/flag-800.png";
                        break;
                    case "Spain":
                        url = "https://cdn.countryflags.com/thumbs/spain/flag-800.png";
                        break;
                    case "Canada":
                        url = "https://cdn.countryflags.com/thumbs/canada/flag-800.png";
                        break;
                    case "Switzerland":
                        url = "https://cdn.countryflags.com/thumbs/switzerland/flag-800.png";
                        break;
                    case "Colombia":
                        url = "https://cdn.countryflags.com/thumbs/colombia/flag-800.png";
                        break;
                    case "Brazil":
                        url = "https://cdn.countryflags.com/thumbs/brazil/flag-800.png";
                        break;
                    case "Haiti":
                        url = "https://cdn.countryflags.com/thumbs/haiti/flag-800.png";
                        break;
                    case "United Kingdom":
                        url = "https://cdn.countryflags.com/thumbs/england/flag-800.png";
                        break;
                    case "Denmark":
                        url = "https://cdn.countryflags.com/thumbs/denmark/flag-800.png";
                        break;
                    case "Netherlands":
                        url = "https://cdn.countryflags.com/thumbs/netherlands/flag-800.png";
                        break;
                    case "Ireland":
                        url = "https://cdn.countryflags.com/thumbs/ireland/flag-800.png";
                        break;
                    case "Indonesia":
                        url = "https://cdn.countryflags.com/thumbs/indonesia/flag-800.png";
                        break;
                    case "Turkey":
                        url = "https://cdn.countryflags.com/thumbs/turkey/flag-800.png";
                        break;
                    case "Norway":
                        url = "https://cdn.countryflags.com/thumbs/norway/flag-800.png";
                        break;
                    case "Mexico":
                        url = "https://cdn.countryflags.com/thumbs/mexico/flag-800.png";
                        break;
                    case "Uruguay":
                        url = "https://cdn.countryflags.com/thumbs/uruguay/flag-800.png";
                        break;
                    case "Russia":
                        url = "https://cdn.countryflags.com/thumbs/russia/flag-800.png";
                        break;
                    case "Poland":
                        url = "https://cdn.countryflags.com/thumbs/poland/flag-800.png";
                        break;
                    case "Singapore":
                        url = "https://cdn.countryflags.com/thumbs/singapore/flag-800.png";
                        break;
                    case "Romania":
                        url = "https://cdn.countryflags.com/thumbs/romania/flag-800.png";
                        break;
                    case "Kenya":
                        url = "https://cdn.countryflags.com/thumbs/kenya/flag-800.png";
                        break;
                    case "Georgia":
                        url ="https://cdn.countryflags.com/thumbs/georgia/flag-800.png";
                        break;
                    default:
                        url = "https://w7.pngwing.com/pngs/931/997/png-transparent-computer-icons-global-miscellaneous-photography-logo.png";
                        break;
                }
                Picasso.get()
                        .load(url)
                        .into(viewHolder.iv_demand);
                break;
        }


    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemViewType(int position) {
        if(arraylist.get(position) instanceof AdView){
            return Constants.ITEM_BANNER_AD;
        }else{
            return Constants.ITEM_CITY;
        }
    }

    private String getID(int pos) {
        ItemCity item = (ItemCity) arraylist.get(pos);
        return item.getId();
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
                ArrayList<ItemCity> filteredItems = new ArrayList<>();

                for (int i = 0, l = filteredArrayList.size(); i < l; i++) {
                    Object obj = filteredArrayList.get(i);
                    if(obj instanceof ItemCity){
                        ItemCity item = (ItemCity) obj;
                        String nameList = item.getName();
                        if (nameList.toLowerCase().contains(constraint))
                            filteredItems.add(item);
                    }
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            } else {
                synchronized (this) {
                    ArrayList<ItemCity> filteredItems = new ArrayList<>();
                    for (Object obj:
                            filteredArrayList) {
                        if(obj instanceof ItemCity){
                            ItemCity item = (ItemCity) obj;
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
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            arraylist = (ArrayList<Object>) results.values;
            notifyDataSetChanged();
        }
    }

    private void loadCityRadio(int pos) {
        int reali = getPosition(getID(pos));
        Constants.itemCity = (ItemCity) arraylist.get(reali);
        cityClickListener.onClick();
    }

    private int getPosition(String id) {
        int count = 0;
        int rid = Integer.parseInt(id);
        for (int i = 0; i < arraylist.size(); i++) {
            try {
                ItemCity item = (ItemCity) arraylist.get(i);
                if (rid == Integer.parseInt(item.getId())) {
                    count = i;
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return count;
    }

    private InterAdListener interAdListener = new InterAdListener() {
        @Override
        public void onClick(int position, String type) {
            loadCityRadio(position);
        }
    };
}