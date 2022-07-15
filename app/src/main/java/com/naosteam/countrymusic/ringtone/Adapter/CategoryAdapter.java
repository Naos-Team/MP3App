package com.naosteam.countrymusic.ringtone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.Ad;
import com.google.android.gms.ads.AdView;
import com.naosteam.countrymusic.mp3.adapter.AdapterGenres;
import com.naosteam.countrymusic.radio.utils.Constants;
import com.naosteam.countrymusic.ringtone.item.ItemCat;
import com.squareup.picasso.Picasso;
import com.naosteam.countrymusic.R;
import com.naosteam.countrymusic.mp3.interfaces.InterScreenListener;
import com.naosteam.countrymusic.mp3.utils.Methods;
import com.naosteam.countrymusic.ringtone.Activity.CategoriesActivity;
import com.naosteam.countrymusic.ringtone.item.ListltemCategory;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter {

    private List<Object> listltems;
    private Context context;
    private Methods methods;
    private int DEFAULT_ITEM = 345;

    public CategoryAdapter(List<Object> listltems, Context context) {
        this.listltems = listltems;
        this.context = context;
    }

    public static class BannerAdsViewHolder extends RecyclerView.ViewHolder {
        public BannerAdsViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == Constants.ITEM_BANNER_AD) {
            View bannerAdView = LayoutInflater.from(parent.getContext()).inflate(R.layout.banner_ad_containter, parent, false);
            return new BannerAdsViewHolder(bannerAdView);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_category_ringtone, parent, false);
            return new MyViewHolder(v);
        }


    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public ImageView imageView, thiva, logo;
        public LinearLayout linearLayout;
        public ConstraintLayout cat_layout;

        public MyViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            imageView = (ImageView) itemView.findViewById(R.id.image);

            thiva = itemView.findViewById(R.id.thiva);

            logo = itemView.findViewById(R.id.logo);
            cat_layout = itemView.findViewById(R.id.category_ringtone);
            //on item click
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        methods.showInterScreenAd(new InterScreenListener() {
                            @Override
                            public void onClick() {
                                Intent intent = new Intent(context, CategoriesActivity.class);
                                intent.putExtra("name", ((ListltemCategory) listltems.get(pos)).getCategory_name());
                                intent.putExtra("cid", ((ListltemCategory) listltems.get(pos)).getCid());
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                        });

                    }
                }
            });


        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof MyViewHolder) {

            MyViewHolder myViewHolder = (MyViewHolder) holder;

            final ListltemCategory listltem = (ListltemCategory) listltems.get(position);

            methods = new Methods(context);

            int width = context.getResources().getDisplayMetrics().widthPixels;
            int height = context.getResources().getDisplayMetrics().heightPixels;
            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams((int) Math.round(width * 0.45), (int) Math.round(height * 0.185));
            layoutParams.setMargins(10, 20, 10, 20);
            myViewHolder.cat_layout.setLayoutParams(layoutParams);

            myViewHolder.name.setText(listltem.getCategory_name());

            //load album cover using picasso

            Log.e("ringtone", "ringtone url: " + listltem.getCategory_image_thumb());

            Picasso.get()
                    .load(listltem.getCategory_image_thumb())
                    .placeholder(R.drawable.placeholder)
                    .into(myViewHolder.imageView);

            int step = 1;
            int final_step = 1;
            for (int i = 1; i < position + 1; i++) {
                if (i == position + 1) {
                    final_step = step;
                }
                step++;
                if (step > 7) {
                    step = 1;
                }
            }

            switch (step) {
                case 1:
                    Picasso.get()
                            .load(R.drawable.gradient_slide_1)
                            .placeholder(R.drawable.gradient_slide_1)
                            .into(myViewHolder.thiva);

                    Picasso.get()
                            .load(R.drawable.ic_thiva_1)
                            .placeholder(R.drawable.ic_thiva_1)
                            .into(myViewHolder.logo);
                    break;
                case 2:
                    Picasso.get()
                            .load(R.drawable.gradient_slide_2)
                            .placeholder(R.drawable.gradient_slide_2)
                            .into(myViewHolder.thiva);

                    Picasso.get()
                            .load(R.drawable.ic_thiva_2)
                            .placeholder(R.drawable.ic_thiva_2)
                            .into(myViewHolder.logo);
                    break;
                case 3:
                    Picasso.get()
                            .load(R.drawable.gradient_slide_3)
                            .placeholder(R.drawable.gradient_slide_3)
                            .into(myViewHolder.thiva);

                    Picasso.get()
                            .load(R.drawable.ic_thiva_3)
                            .placeholder(R.drawable.ic_thiva_3)
                            .into(myViewHolder.logo);
                    break;
                case 4:
                    Picasso.get()
                            .load(R.drawable.gradient_slide_4)
                            .placeholder(R.drawable.gradient_slide_4)
                            .into(myViewHolder.thiva);

                    Picasso.get()
                            .load(R.drawable.ic_thiva_4)
                            .placeholder(R.drawable.ic_thiva_4)
                            .into(myViewHolder.logo);
                    break;
                case 5:
                    Picasso.get()
                            .load(R.drawable.gradient_slide_5)
                            .placeholder(R.drawable.gradient_slide_5)
                            .into(myViewHolder.thiva);

                    Picasso.get()
                            .load(R.drawable.ic_thiva_5)
                            .placeholder(R.drawable.ic_thiva_5)
                            .into(myViewHolder.logo);
                    break;
                case 6:
                    Picasso.get()
                            .load(R.drawable.gradient_slide_6)
                            .placeholder(R.drawable.gradient_slide_6)
                            .into(myViewHolder.thiva);

                    Picasso.get()
                            .load(R.drawable.ic_thiva_6)
                            .placeholder(R.drawable.ic_thiva_6)
                            .into(myViewHolder.logo);
                    break;
                case 7:
                    Picasso.get()
                            .load(R.drawable.gradient_slide_7)
                            .placeholder(R.drawable.gradient_slide_7)
                            .into(myViewHolder.thiva);

                    Picasso.get()
                            .load(R.drawable.ic_thiva_7)
                            .placeholder(R.drawable.ic_thiva_7)
                            .into(myViewHolder.logo);
                    break;
            }


        }else if(holder instanceof BannerAdsViewHolder){
            BannerAdsViewHolder adsViewHolder = (BannerAdsViewHolder) holder;
            AdView adView = (AdView) listltems.get(position);
            ViewGroup adCardView = (ViewGroup) adsViewHolder.itemView;

            if(adCardView.getChildCount()>0){
                adCardView.removeAllViews();
            }
            adCardView.addView(adView);
        }


    }

    @Override
    public int getItemViewType(int position) {

        if(listltems.get(position) instanceof AdView){
            return Constants.ITEM_BANNER_AD;
        }else{
            return DEFAULT_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return listltems.size();
    }

}
