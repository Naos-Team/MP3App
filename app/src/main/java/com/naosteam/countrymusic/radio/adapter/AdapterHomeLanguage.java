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

import com.squareup.picasso.Picasso;
import com.naosteam.countrymusic.R;
import com.naosteam.countrymusic.radio.activity.RadioBaseActivity;
import com.naosteam.countrymusic.radio.interfaces.CityClickListener;
import com.naosteam.countrymusic.mp3.interfaces.InterAdListener;
import com.naosteam.countrymusic.radio.item.ItemLanguage;
import com.naosteam.countrymusic.radio.utils.Constants;
import com.naosteam.countrymusic.mp3.utils.Methods;
import com.naosteam.countrymusic.radio.utils.SharedPref;

import java.util.ArrayList;

public class AdapterHomeLanguage extends RecyclerView.Adapter<AdapterHomeLanguage.MyViewHolder> {

    private ArrayList<ItemLanguage> arraylist;
    private ArrayList<ItemLanguage> filteredArrayList;
    private NameFilter filter;
    private CityClickListener cityClickListener;
    private Methods methods;
    private Context context;
    private SharedPref sharedPref;
    private ConstraintLayout.LayoutParams lp_item;

    public AdapterHomeLanguage(Context context, ArrayList<ItemLanguage> list, CityClickListener cityClickListener, ConstraintLayout.LayoutParams lp_item) {
        this.arraylist = list;
        this.filteredArrayList = list;
        this.cityClickListener = cityClickListener;
        methods = new Methods(context, interAdListener);
        sharedPref = new SharedPref(context);
        this.lp_item = lp_item;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cityhome, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        holder.tv_city.setVisibility(View.GONE);
        holder.tv_country.setText(arraylist.get(position).getName());

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((RadioBaseActivity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        holder.tv_country.setTypeface(null, Typeface.BOLD);
        holder.tv_country.setTextSize(TypedValue.COMPLEX_UNIT_PX, height*0.02f);

        holder.cs_item.setLayoutParams(lp_item);

        holder.cs_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methods.showInterAd(holder.getAdapterPosition(), "");
            }
        });

        String url ="";

        switch (arraylist.get(position).getName()){
            case "English":
                url = "https://cdn.countryflags.com/thumbs/england/flag-800.png";
                break;
            case "German":
                url = "https://cdn.countryflags.com/thumbs/germany/flag-800.png";
                break;
            case "Italian":
                url = "https://cdn.countryflags.com/thumbs/italy/flag-800.png";
                break;
            case "Portuguese":
                url = "https://cdn.countryflags.com/thumbs/portugal/flag-800.png";
                break;
            case "Australia":
                url = "https://cdn.countryflags.com/thumbs/australia/flag-800.png";
                break;
            case "Austria":
                url = "https://cdn.countryflags.com/thumbs/austria/flag-800.png";
                break;
            case "Swedish":
                url = "https://cdn.countryflags.com/thumbs/sweden/flag-800.png";
                break;
            case "French":
                url = "https://cdn.countryflags.com/thumbs/france/flag-800.png";
                break;
            case "Spanish":
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
            case "Danish":
                url = "https://cdn.countryflags.com/thumbs/denmark/flag-800.png";
                break;
            case "Dutch":
                url = "https://cdn.countryflags.com/thumbs/netherlands/flag-800.png";
                break;
            case "Ireland":
                url = "https://cdn.countryflags.com/thumbs/ireland/flag-800.png";
                break;
            case "Indonesian":
                url = "https://cdn.countryflags.com/thumbs/indonesia/flag-800.png";
                break;
            case "Turkish":
                url = "https://cdn.countryflags.com/thumbs/turkey/flag-800.png";
                break;
            case "Norwegian":
                url = "https://cdn.countryflags.com/thumbs/norway/flag-800.png";
                break;
            case "Mexico":
                url = "https://cdn.countryflags.com/thumbs/mexico/flag-800.png";
                break;
            case "Uruguay":
                url = "https://cdn.countryflags.com/thumbs/uruguay/flag-800.png";
                break;
            case "Russian":
                url = "https://cdn.countryflags.com/thumbs/russia/flag-800.png";
                break;
            case "Polish":
                url = "https://cdn.countryflags.com/thumbs/poland/flag-800.png";
                break;
            case "Singapore":
                url = "https://cdn.countryflags.com/thumbs/singapore/flag-800.png";
                break;
            case "Romanian":
                url = "https://cdn.countryflags.com/thumbs/romania/flag-800.png";
                break;
            case "Georgian":
                url ="https://cdn.countryflags.com/thumbs/georgia/flag-800.png";
                break;
            default:
                url = "https://cdn-icons-png.flaticon.com/512/814/814513.png";
                break;
        }
        Picasso.get()
                .load(url)
                .placeholder(R.drawable.ic_worldwide)
                .into(holder.image_city_item);
    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_country, tv_city;
        private ImageView image_city_item;
        private ConstraintLayout cs_item;

        private MyViewHolder(View view) {
            super(view);
            image_city_item = view.findViewById(R.id.image_city_item);
            cs_item = view.findViewById(R.id.cs_item);
            tv_country = view.findViewById(R.id.tv_cityhome_text);
            tv_city = view.findViewById(R.id.tv_cityhome_text_city);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    private String getID(int pos) {
        return arraylist.get(pos).getId();
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
                ArrayList<ItemLanguage> filteredItems = new ArrayList<>();

                for (int i = 0, l = filteredArrayList.size(); i < l; i++) {
                    String nameList = filteredArrayList.get(i).getName();
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

            arraylist = (ArrayList<ItemLanguage>) results.values;
            notifyDataSetChanged();
        }
    }

    private void loadLangRadio(int pos) {
        int reali = getPosition(getID(pos));
        Constants.itemLanguage = arraylist.get(reali);
        cityClickListener.onClick();
    }

    private int getPosition(String id) {
        int count = 0;
        int rid = Integer.parseInt(id);
        for (int i = 0; i < arraylist.size(); i++) {
            try {
                if (rid == Integer.parseInt(arraylist.get(i).getId())) {
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
            loadLangRadio(position);
        }
    };
}
