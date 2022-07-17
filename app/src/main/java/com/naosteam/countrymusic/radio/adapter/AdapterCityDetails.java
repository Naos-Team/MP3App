package com.naosteam.countrymusic.radio.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class AdapterCityDetails extends RecyclerView.Adapter<AdapterCityDetails.MyViewHolder> {

    private DBHelper dbHelper;
    private Context context;
    private ArrayList<ItemRadio> arraylist;
    private ArrayList<ItemRadio> filteredArrayList;
    private NameFilter filter;
    private Methods methods;
    private com.naosteam.countrymusic.mp3.utils.Methods mp3_method;
    private ConstraintLayout.LayoutParams lp_item;

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

    public AdapterCityDetails(Context context, ArrayList<ItemRadio> list, ConstraintLayout.LayoutParams lp_item) {
        this.context = context;
        this.arraylist = list;
        this.filteredArrayList = list;
        dbHelper = new DBHelper(context);

        methods = new Methods(context);
        mp3_method = new com.naosteam.countrymusic.mp3.utils.Methods(context, interAdListener);
        this.lp_item = lp_item;
        Resources r = context.getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Constants.GRID_PADDING, r.getDisplayMetrics());
        Constants.columnWidth = (int) ((methods.getScreenWidth() - ((Constants.NUM_OF_COLUMNS + 1) * padding)) / Constants.NUM_OF_COLUMNS);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        try{
             itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_radiolist_grid, parent, false);
            return new MyViewHolder(itemView);
        }
        catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        ItemRadio objAllBean = (ItemRadio) arraylist.get(position);
        holder.textView_radio.setText(objAllBean.getRadioName());
        holder.textView_freq.setText(objAllBean.getRadioFreq());

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        holder.textView_radio.setTypeface(null, Typeface.BOLD);
        holder.textView_radio.setTextSize(TypedValue.COMPLEX_UNIT_PX, height*0.02f);
        holder.textView_freq.setTextSize(TypedValue.COMPLEX_UNIT_PX, height*0.014f);

        String url = methods.getImageThumbSize(arraylist.get(holder.getAdapterPosition()).getRadioImageurl(),"");
        String url1 = "";

        if (url.contains(Constants.BASE_SERVER_URL)){
            url1 = url;
        }
        else{
            url1 = Constants.BASE_SERVER_URL + url;
        }


        Picasso.get()
                .load(url1)
                .placeholder(R.drawable.placeholder)
                .into(holder.imageView);

        holder.cs_item.setLayoutParams(lp_item);

        holder.cs_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp3_method.showInterAd(holder.getAdapterPosition(), "");
            }
        });

    }

    private Boolean checkFav(int pos) {
        return dbHelper.checkFav(arraylist.get(pos));
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
                    String nameList = filteredArrayList.get(i).getRadioName();
                    if (nameList.toLowerCase().contains(constraint))
                        filteredItems.add(filteredArrayList.get(i));
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            } else {
                synchronized (this) {
                    result.values = filteredArrayList;
                    result.count = filteredArrayList.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            arraylist = (ArrayList<ItemRadio>) results.values;
            notifyDataSetChanged();
        }
    }

    private InterAdListener interAdListener = new InterAdListener() {
        @Override
        public void onClick(int position, String type) {
            Constants.arrayList_radio.clear();
            Constants.arrayList_radio.addAll(arraylist);
            ((RadioBaseActivity) context).clickPlay(position, true);
        }
    };
}