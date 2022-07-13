package com.naosteam.countrymusic.ringtone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.naosteam.countrymusic.R;
import com.naosteam.countrymusic.mp3.interfaces.InterScreenListener;
import com.naosteam.countrymusic.mp3.utils.Methods;
import com.naosteam.countrymusic.ringtone.Activity.CategoriesActivity;
import com.naosteam.countrymusic.ringtone.item.ListltemCategory;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<ListltemCategory> listltems;
    private Context context;
    private Methods methods;

    public CategoryAdapter(List<ListltemCategory> listltems, Context context) {
        this.listltems = listltems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_category_ringtone,parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ListltemCategory listltem = listltems.get(position);

        int width = context.getResources().getDisplayMetrics().widthPixels;
        int height = context.getResources().getDisplayMetrics().heightPixels;
        ConstraintLayout.LayoutParams layoutParams= new ConstraintLayout.LayoutParams((int)Math.round(width*0.45), (int)Math.round(height*0.185));
        layoutParams.setMargins(10,20,10,20);
        holder.cat_layout.setLayoutParams(layoutParams);

        holder.name.setText(listltem.getCategory_name());

        //load album cover using picasso
        Picasso.get()
                .load(listltem.getCategory_image_thumb())
                .placeholder(R.color.colorAccent)
                .into(holder.imageView);

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
                        .into(holder.thiva);

                Picasso.get()
                        .load(R.drawable.ic_thiva_1)
                        .placeholder(R.drawable.ic_thiva_1)
                        .into(holder.logo);
                break;
            case 2:
                Picasso.get()
                        .load(R.drawable.gradient_slide_2)
                        .placeholder(R.drawable.gradient_slide_2)
                        .into(holder.thiva);

                Picasso.get()
                        .load(R.drawable.ic_thiva_2)
                        .placeholder(R.drawable.ic_thiva_2)
                        .into(holder.logo);
                break;
            case 3:
                Picasso.get()
                        .load(R.drawable.gradient_slide_3)
                        .placeholder(R.drawable.gradient_slide_3)
                        .into(holder.thiva);

                Picasso.get()
                        .load(R.drawable.ic_thiva_3)
                        .placeholder(R.drawable.ic_thiva_3)
                        .into(holder.logo);
                break;
            case 4:
                Picasso.get()
                        .load(R.drawable.gradient_slide_4)
                        .placeholder(R.drawable.gradient_slide_4)
                        .into(holder.thiva);

                Picasso.get()
                        .load(R.drawable.ic_thiva_4)
                        .placeholder(R.drawable.ic_thiva_4)
                        .into(holder.logo);
                break;
            case 5:
                Picasso.get()
                        .load(R.drawable.gradient_slide_5)
                        .placeholder(R.drawable.gradient_slide_5)
                        .into(holder.thiva);

                Picasso.get()
                        .load(R.drawable.ic_thiva_5)
                        .placeholder(R.drawable.ic_thiva_5)
                        .into(holder.logo);
                break;
            case 6:
                Picasso.get()
                        .load(R.drawable.gradient_slide_6)
                        .placeholder(R.drawable.gradient_slide_6)
                        .into(holder.thiva);

                Picasso.get()
                        .load(R.drawable.ic_thiva_6)
                        .placeholder(R.drawable.ic_thiva_6)
                        .into(holder.logo);
                break;
            case 7:
                Picasso.get()
                        .load(R.drawable.gradient_slide_7)
                        .placeholder(R.drawable.gradient_slide_7)
                        .into(holder.thiva);

                Picasso.get()
                        .load(R.drawable.ic_thiva_7)
                        .placeholder(R.drawable.ic_thiva_7)
                        .into(holder.logo);
                break;
        }




    }

    @Override
    public int getItemCount() {
        return listltems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView name;
        public ImageView imageView, thiva, logo;
        public LinearLayout linearLayout;
        public ConstraintLayout cat_layout;

        public ViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            imageView = (ImageView) itemView.findViewById(R.id.image);

            thiva = itemView.findViewById(R.id.thiva);

            logo = itemView.findViewById(R.id.logo);
            cat_layout = itemView.findViewById(R.id.category_ringtone);
            //on item click
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION){
                        methods.showInterScreenAd(new InterScreenListener() {
                            @Override
                            public void onClick() {
                                Intent intent = new Intent(context, CategoriesActivity.class);
                                intent.putExtra("name", listltems.get(pos).getCategory_name());
                                intent.putExtra("cid", listltems.get(pos).getCid());
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                        });

                    }
                }
            });



        }
    }



}
