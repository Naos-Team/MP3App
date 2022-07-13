package com.naosteam.countrymusic.radio.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.PaintDrawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.naosteam.countrymusic.R;
import com.naosteam.countrymusic.radio.activity.RadioBaseActivity;
import com.naosteam.countrymusic.radio.item.ItemOnDemandCat;
import com.naosteam.countrymusic.mp3.utils.Methods;

import java.util.ArrayList;

public class AdapterSlideOnDemand extends RecyclerView.Adapter<AdapterSlideOnDemand.OndemandViewHolder> {
    private ArrayList<ItemOnDemandCat> arrayList;
    private Methods methods;
    private Context context;
    private static int selected_index= 0;

    public AdapterSlideOnDemand(ArrayList<ItemOnDemandCat> arrayList,  Methods methods) {
        this.arrayList = arrayList;
        this.methods = methods;
    }

    @NonNull
    @Override
    public OndemandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.layout_slide_ondemand_item, parent, false);
        return new OndemandViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OndemandViewHolder holder, int position) {
        holder.bindView(position);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class OndemandViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_title;
        private RoundedImageView imageView_ondemand2_slide_item, imageView_transparency_slide_item;
        private ConstraintLayout layout_item_slide, cs_title;

        public OndemandViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            cs_title = itemView.findViewById(R.id.cs_title);
            imageView_ondemand2_slide_item = itemView.findViewById(R.id.imageView_ondemand2_slide_item);
            imageView_transparency_slide_item =  itemView.findViewById(R.id.imageView_transparency_slide_item);
            layout_item_slide = itemView.findViewById(R.id.layout_item_slide);
        }

        public void bindView(int position){
            tv_title.setText(arrayList.get(position).getName());

            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((RadioBaseActivity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;

            PaintDrawable paintDrawable = new PaintDrawable();
            paintDrawable.setCornerRadius(width*0.02f);
            paintDrawable.setTint(context.getResources().getColor(R.color.bg_radius_ondemand));
            cs_title.setBackground(paintDrawable);

//            imageView_ondemand2_slide_item.setCornerRadius(width*0.07f);

            tv_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, height*0.018f);
            tv_title.setTypeface(null, Typeface.BOLD);

            Picasso.get().load(arrayList.get(position).getImage()).into(imageView_ondemand2_slide_item);
            if(position == selected_index){
                //itemView.setScaleX(1.3f);
                imageView_transparency_slide_item.setVisibility(View.GONE);
            } else {

                //itemView.setScaleX(1.0f);
                imageView_transparency_slide_item.setVisibility(View.VISIBLE);
            }
            layout_item_slide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    methods.showInterAd(position, "");
                }
            });
        }
    }

    public static void setSelected_index(int selected_index) {
        AdapterSlideOnDemand.selected_index = selected_index;
    }
}
