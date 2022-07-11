package com.zxfdwka.bestcountrymusic.radio.adapter;

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

import com.zxfdwka.bestcountrymusic.R;
import com.zxfdwka.bestcountrymusic.radio.activity.RadioBaseActivity;
import com.zxfdwka.bestcountrymusic.radio.interfaces.InterAdListener;
import com.zxfdwka.bestcountrymusic.radio.item.ItemRadio;
import com.zxfdwka.bestcountrymusic.radio.utils.Constants;
import com.zxfdwka.bestcountrymusic.radio.utils.DBHelper;
import com.zxfdwka.bestcountrymusic.radio.utils.Methods;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;


public class AdapterRadios extends RecyclerView.Adapter {

    private DBHelper dbHelper;
    private Context context;
    private ArrayList<Object> arraylist;
    private ArrayList<Object> filteredArrayList;
    private NameFilter filter;
    private Methods methods;
    private ConstraintLayout.LayoutParams lp_item;

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    public AdapterRadios(Context context, ArrayList<Object> list, ConstraintLayout.LayoutParams lp_item) {
        this.context = context;
        this.arraylist = list;
        this.filteredArrayList = list;
        dbHelper = new DBHelper(context);
        methods = new Methods(context, interAdListener);
        this.lp_item = lp_item;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

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

    class BannerAdsViewHolder extends RecyclerView.ViewHolder{

        public BannerAdsViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }

    private static class ProgressViewHolder extends RecyclerView.ViewHolder {
        private static CircularProgressBar progressBar;

        private ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case VIEW_ITEM:
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_radiolist_grid, parent, false);
                return new MyViewHolder(itemView);
            case Constants.ITEM_BANNER_AD:
                View bannerAdView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.banner_ad_containter, parent, false);
                return new BannerAdsViewHolder(bannerAdView);
            default:
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_progressbar, parent, false);
                return new ProgressViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int position) {

        int viewType = getItemViewType(position);

        switch (viewType){
            case Constants.ITEM_BANNER_AD:
                BannerAdsViewHolder adsViewHolder = (BannerAdsViewHolder) viewHolder;
                AdView adView = (AdView) arraylist.get(position);
                ViewGroup adCardView = (ViewGroup) adsViewHolder.itemView;

                if(adCardView.getChildCount()>0){
                    adCardView.removeAllViews();
                }
                if(adCardView.getParent() != null){
                    ((ViewGroup)adView.getParent()).removeView(adView);
                }
                adCardView.addView(adView);
                break;
            case VIEW_ITEM:
                final MyViewHolder holder = (MyViewHolder) viewHolder;
                Boolean isFav = checkFav(position);

//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Constants.columnWidth, Constants.columnWidth);
//                params.setMargins(0, 0, 0, 20);
//                holder.cardView.setLayoutParams(params);
//                if (isFav) {
//                    holder.imageView_fav.setImageResource(R.drawable.radio_fav);
//                } else {
//                    holder.imageView_fav.setImageResource(R.drawable.radio_unfav);
//                }
                ItemRadio objAllBean = (ItemRadio) arraylist.get(position);
                holder.textView_radio.setText(objAllBean.getRadioName());
                holder.textView_freq.setText(objAllBean.getRadioFreq());

                DisplayMetrics displayMetrics = new DisplayMetrics();
                ((RadioBaseActivity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = displayMetrics.heightPixels;
                int width = displayMetrics.widthPixels;

                holder.textView_radio.setTypeface(null, Typeface.BOLD);
                holder.textView_radio.setTextSize(TypedValue.COMPLEX_UNIT_PX, height*0.02f);
                holder.textView_freq.setTextSize(TypedValue.COMPLEX_UNIT_PX, height*0.014f);

                Picasso.get()
                        .load(methods.getImageThumbSize(objAllBean.getRadioImageurl().replace(" ", "%20"),context.getString(R.string.home)))
                        .placeholder(R.drawable.placeholder)
                        .into(holder.imageView);

                holder.cs_item.setLayoutParams(lp_item);

                holder.cs_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        methods.showInter(holder.getAdapterPosition(), "");
                    }
                });

//                holder.imageView_fav.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if (dbHelper.addORremoveFav(itemPosition)) {
//                            holder.imageView_fav.setImageResource(R.drawable.radio_fav);
//                            methods.showToast(context.getString(R.string.add_to_fav));
//                        } else {
//                            holder.imageView_fav.setImageResource(R.drawable.radio_unfav);
//                            methods.showToast(context.getString(R.string.remove_from_fav));
//                        }
//                    }
//                });
                break;
            default:
                if (getItemCount() < 15) {
                    ProgressViewHolder.progressBar.setVisibility(View.GONE);
                }
                break;
        }
    }

    private Boolean checkFav(int pos) {
        return dbHelper.checkFav((ItemRadio) arraylist.get(pos));
    }

    public void hideHeader() {
        try {
            ProgressViewHolder.progressBar.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isHeader(int position) {
        return position == arraylist.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(arraylist.get(position) instanceof AdView){
            return Constants.ITEM_BANNER_AD;
        }else{
            if(position < arraylist.size()) {
                return VIEW_ITEM;
            } else {
                return VIEW_PROG;
            }
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
                        ItemRadio itemRadio = (ItemRadio) obj;
                        String nameList = itemRadio.getRadioName();
                        if (nameList.toLowerCase().contains(constraint))
                            filteredItems.add(itemRadio);
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
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            arraylist = (ArrayList<Object>) results.values;
            notifyDataSetChanged();
        }
    }

    private InterAdListener interAdListener = new InterAdListener() {
        @Override
        public void onClick(int position, String type) {
            Constants.arrayList_radio.clear();
            for (Object o : arraylist){
                if(o instanceof ItemRadio){
                    Constants.arrayList_radio.add((ItemRadio) o);
                }
            }
            ((RadioBaseActivity) context).clickPlay(position, true);
        }
    };
}