package com.zxfdwka.bestcountrymusic.radio.adapter;

import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.PaintDrawable;
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

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.zxfdwka.bestcountrymusic.R;
import com.zxfdwka.bestcountrymusic.radio.activity.RadioBaseActivity;
import com.zxfdwka.bestcountrymusic.radio.item.ItemOnDemandCat;
import com.zxfdwka.bestcountrymusic.radio.utils.Methods;

import java.util.ArrayList;


public class AdapterOnDemandCat extends RecyclerView.Adapter<AdapterOnDemandCat.MyViewHolder> {

    private Context context;
    private ArrayList<ItemOnDemandCat> arraylist;
    private ArrayList<ItemOnDemandCat> filteredArrayList;
    private NameFilter filter;
    private Methods methods;
    private ConstraintLayout.LayoutParams lp_item;

    class MyViewHolder extends RecyclerView.ViewHolder {

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

    public AdapterOnDemandCat(Context context, ArrayList<ItemOnDemandCat> list, Methods methods, ConstraintLayout.LayoutParams lp_item) {
        this.context = context;
        this.arraylist = list;
        this.filteredArrayList = list;
        this.lp_item = lp_item;
        this.methods = methods;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_ondemandcat2, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        holder.tv_title.setText(arraylist.get(position).getName());
        holder.tv_number.setText(arraylist.get(position).getTotalItems() + " Tracks");

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((RadioBaseActivity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        PaintDrawable paintDrawable = new PaintDrawable();
        paintDrawable.setCornerRadius(width*0.06f);
        paintDrawable.setTint(context.getResources().getColor(R.color.bg_radius_ondemand));
        holder.cs_title.setBackground(paintDrawable);

        holder.iv_demand.setCornerRadius(width*0.07f);

        holder.tv_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, height*0.018f);
        holder.tv_number.setTextSize(TypedValue.COMPLEX_UNIT_PX, height*0.015f);
        holder.tv_title.setTypeface(null, Typeface.BOLD);

        holder.cs_item.setLayoutParams(lp_item);

        Picasso.get()
                .load(methods.getImageThumbSize(arraylist.get(holder.getAdapterPosition()).getImage(),context.getString(R.string.on_demand)))
                .into(holder.iv_demand);
    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public String getID(int pos) {
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
                ArrayList<ItemOnDemandCat> filteredItems = new ArrayList<>();

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

            arraylist = (ArrayList<ItemOnDemandCat>) results.values;
            notifyDataSetChanged();
        }
    }
}