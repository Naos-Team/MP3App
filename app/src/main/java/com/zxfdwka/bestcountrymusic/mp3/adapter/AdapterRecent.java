package com.zxfdwka.bestcountrymusic.mp3.adapter;

import static android.Manifest.permission.READ_PHONE_STATE;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.zxfdwka.bestcountrymusic.mp3.asyncTask.GetRating;
import com.zxfdwka.bestcountrymusic.mp3.asyncTask.LoadRating;
import com.zxfdwka.bestcountrymusic.mp3.fragment.FragmentOptionMusic;
import com.zxfdwka.bestcountrymusic.mp3.interfaces.ClickListenerPlayList;
import com.zxfdwka.bestcountrymusic.mp3.interfaces.OptionMusicListener;
import com.zxfdwka.bestcountrymusic.mp3.interfaces.RatingListener;
import com.zxfdwka.bestcountrymusic.mp3.item.ItemMyPlayList;
import com.zxfdwka.bestcountrymusic.mp3.item.ItemSong;
import com.zxfdwka.bestcountrymusic.R;
import com.zxfdwka.bestcountrymusic.mp3.utils.Constant;
import com.zxfdwka.bestcountrymusic.mp3.utils.GlobalBus;
import com.zxfdwka.bestcountrymusic.mp3.utils.Methods;

import java.util.ArrayList;

public class AdapterRecent extends RecyclerView.Adapter<AdapterRecent.MyViewHolder> {

    private Methods methods;
    private Context context;
    private ArrayList<ItemSong> arrayList;
    private ClickListenerPlayList clickListenerPlayList;
    private Dialog dialog_rate;
    private BottomSheetDialog dialog_desc;

    class MyViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView iv_song;
        ImageView iv_more;
        TextView tv_title, tv_cat;
        ConstraintLayout layout_recent;

        MyViewHolder(View view) {
            super(view);
            iv_song = view.findViewById(R.id.iv_recent);
            iv_more = view.findViewById(R.id.iv_recent_more);
            tv_title = view.findViewById(R.id.tv_recent_song);
            tv_cat = view.findViewById(R.id.tv_recent_cat);
            layout_recent = view.findViewById(R.id.layout_recent);
        }
    }

    public AdapterRecent(Context context, ArrayList<ItemSong> arrayList, ClickListenerPlayList clickListenerPlayList) {
        this.context = context;
        this.arrayList = arrayList;
        this.clickListenerPlayList = clickListenerPlayList;
        methods = new Methods(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_recent, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        holder.tv_title.setText(arrayList.get(position).getTitle());
        holder.tv_cat.setText(arrayList.get(position).getCatName());
        Picasso.get()
                .load(arrayList.get(position).getImageBig())
                .placeholder(R.drawable.placeholder_song)
                .into(holder.iv_song);

        int index = position;
        holder.iv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                openOptionPopUp(holder.iv_more, holder.getAdapterPosition());
                FragmentOptionMusic fragmentOptionMusic = new FragmentOptionMusic(arrayList.get(index), new OptionMusicListener() {
                    @Override
                    public void onDescription(ItemSong itemSong) {
                        View view = ((Activity) context).getLayoutInflater().inflate(R.layout.layout_desc, null);

                        dialog_desc = new BottomSheetDialog(context);
                        dialog_desc.setContentView(view);
                        dialog_desc.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
                        dialog_desc.show();

                        AppCompatButton button = dialog_desc.findViewById(R.id.button_detail_close);
                        TextView textView = dialog_desc.findViewById(R.id.tv_desc_title);
                        textView.setText(itemSong.getTitle());

                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog_desc.dismiss();
                            }
                        });

                        WebView webview_song_desc = dialog_desc.findViewById(R.id.webView_bottom);
                        String mimeType = "text/html;charset=UTF-8";
                        String encoding = "utf-8";
                        String text = "<html><head>"
                                + "<style> body{color: #000 !important;text-align:left}"
                                + "</style></head>"
                                + "<body>"
                                + itemSong.getDescription()
                                + "</body></html>";

//                            webview_song_desc.loadData(text, mimeType, encoding);
                        webview_song_desc.loadDataWithBaseURL("blarg://ignored", text, mimeType, encoding, "");
                    }

                    @Override
                    public void onLike(ItemSong itemSong, View view) {
                        if (Constant.isLogged) {
                            if (Constant.arrayList_play.size() > 0) {
                                if (Constant.isOnline) {
                                    methods.animateHeartButton(view);
                                    view.setSelected(!view.isSelected());
                                    view.setSelected(view.isSelected());
                                }
                            } else {
                                Toast.makeText(context, context.getResources().getString(R.string.err_no_songs_selected), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            methods.clickLogin();
                        }
                    }

                    @Override
                    public void onAddToPlaylist(ItemSong itemSong) {
                        methods.openPlaylists(itemSong, true);
                    }

                    @Override
                    public void onAddToQueue(ItemSong itemSong) {
                        Constant.arrayList_play.add(itemSong);
                        GlobalBus.getBus().postSticky(new ItemMyPlayList("", "", null));
                        Toast.makeText(context, context.getResources().getString(R.string.add_to_queue), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSearchYTB(ItemSong itemSong) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_SEARCH);
                            intent.setPackage("com.google.android.youtube");
                            intent.putExtra("query", itemSong.getTitle());
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(context, context.getResources().getString(R.string.youtube_not_installed), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onShare(ItemSong itemSong) {
                        if (Constant.isOnline || Constant.isDownloaded) {
                            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                            sharingIntent.setType("text/plain");
                            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.share_song));
                            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, context.getResources().getString(R.string.listening) + " - "
                                    + itemSong.getTitle() + "\n\nvia " + context.getResources().getString(R.string.app_name) + " - http://play.google.com/store/apps/details?id=" + context.getPackageName());
                            context.startActivity(Intent.createChooser(sharingIntent, context.getResources().getString(R.string.share_song)));
                        } else {
                            if (checkPer()) {
                                Intent share = new Intent(Intent.ACTION_SEND);
                                share.setType("audio/mp3");
                                share.putExtra(Intent.EXTRA_STREAM, Uri.parse(itemSong.getUrl()));
                                share.putExtra(android.content.Intent.EXTRA_TEXT, context.getResources().getString(R.string.listening) + " - "
                                        + itemSong.getTitle() + "\n\nvia " + context.getResources().getString(R.string.app_name) + " - http://play.google.com/store/apps/details?id=" + context.getPackageName());
                                context.startActivity(Intent.createChooser(share, context.getResources().getString(R.string.share_song)));
                            }
                        }
                    }

                    @Override
                    public void onRate(ItemSong itemSong) {
                        openRateDialog(itemSong);
                    }

                    @Override
                    public void onEndLike() {
                        notifyItemChanged(index);
                    }
                }, methods, null, false);

                fragmentOptionMusic.show(((FragmentActivity) context).getSupportFragmentManager(), fragmentOptionMusic.getTag());
            }
        });

        holder.iv_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListenerPlayList.onClick(holder.getAdapterPosition());
            }
        });

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                context.getResources().getDisplayMetrics().heightPixels * 185 / 1000,
                context.getResources().getDisplayMetrics().heightPixels * 230 / 1000
        );
        layoutParams.setMargins(context.getResources().getDisplayMetrics().widthPixels*3/100, 5,
                2, 5);
        holder.layout_recent.setLayoutParams(layoutParams);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public Boolean checkPer() {
        if ((ContextCompat.checkSelfPermission(context, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ((Activity) context).requestPermissions(new String[]{READ_PHONE_STATE}, 1);
            }
            return false;
        } else {
            return true;
        }
    }

    private void openRateDialog(ItemSong itemSong) {
        dialog_rate = new Dialog(context);
        dialog_rate.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_rate.setContentView(R.layout.layout_review);

        final ImageView iv_close = dialog_rate.findViewById(R.id.iv_rate_close);
        final TextView textView = dialog_rate.findViewById(R.id.tv_rate);
        final RatingBar ratingBar = dialog_rate.findViewById(R.id.rb_add);
        final Button button = dialog_rate.findViewById(R.id.button_submit_rating);
        final Button button_later = dialog_rate.findViewById(R.id.button_later_rating);

        ratingBar.setStepSize(Float.parseFloat("1"));

        if (itemSong.getUserRating().equals("") || itemSong.getUserRating().equals("0")) {
            new GetRating(new RatingListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onEnd(String success, String isRateSuccess, String message, int rating) {
                    if (rating > 0) {
                        ratingBar.setRating(rating);
                        textView.setText(context.getString(R.string.thanks_for_rating));
                    } else {
                        ratingBar.setRating(1);
                    }
                    itemSong.setUserRating(String.valueOf(rating));

                }
            }, methods.getAPIRequest(Constant.METHOD_SINGLE_SONG, 0, "", itemSong.getId(), "", "", "", "", "", "", "", "", "", "", "", Constant.itemUser.getId(), "", null)).execute();
        } else {
            if (Integer.parseInt(itemSong.getUserRating()) != 0 && !itemSong.getUserRating().equals("")) {
                textView.setText(context.getString(R.string.thanks_for_rating));
                ratingBar.setRating(Integer.parseInt(itemSong.getUserRating()));
            } else {
                ratingBar.setRating(1);
            }
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Constant.isLogged) {
                    if (ratingBar.getRating() != 0) {
                        if (methods.isNetworkAvailable()) {
                            loadRatingApi(String.valueOf((int) ratingBar.getRating()), itemSong);
                        } else {
                            Toast.makeText(context, context.getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, context.getString(R.string.select_rating), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    methods.clickLogin();
                }
            }
        });

        button_later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_rate.dismiss();
            }
        });

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_rate.dismiss();
            }
        });

        dialog_rate.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog_rate.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog_rate.show();
        Window window = dialog_rate.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void loadRatingApi(final String rate, ItemSong itemSong) {

        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getResources().getString(R.string.loading));
        LoadRating loadRating = new LoadRating(new RatingListener() {
            @Override
            public void onStart() {
                progressDialog.show();
            }

            @Override
            public void onEnd(String success, String isRateSuccess, String message, int rating) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (success.equals("1")) {
                    if (isRateSuccess.equals("1")) {
                        itemSong.setAverageRating(String.valueOf(rating));
                        itemSong.setTotalRate(String.valueOf(Integer.parseInt(itemSong.getTotalRate() + 1)));
                        itemSong.setUserRating(String.valueOf(rate));
                    }
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                }
                dialog_rate.dismiss();
            }
        }, methods.getAPIRequest(Constant.METHOD_RATINGS, 0, "", itemSong.getId(), "", "", "", "", "", "", rate, "", "", "", "", Constant.itemUser.getId(), "", null));

        loadRating.execute();
    }

    private void openOptionPopUp(ImageView imageView, final int pos) {
        PopupMenu popup = new PopupMenu(context, imageView);
        popup.getMenuInflater().inflate(R.menu.popup_song, popup.getMenu());

        if (!Constant.isOnline) {
            popup.getMenu().findItem(R.id.popup_add_queue).setVisible(false);
        }
        if(!Constant.isSongDownload) {
            popup.getMenu().findItem(R.id.popup_download).setVisible(false);
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.popup_add_song:
                        methods.openPlaylists(arrayList.get(pos), true);
                        break;
                    case R.id.popup_add_queue:
                        Constant.arrayList_play.add(arrayList.get(pos));
                        GlobalBus.getBus().postSticky(new ItemMyPlayList("", "", null));
                        Toast.makeText(context, context.getString(R.string.add_to_queue), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.popup_youtube:
                        try {
                            Intent intent = new Intent(Intent.ACTION_SEARCH);
                            intent.setPackage("com.google.android.youtube");
                            intent.putExtra("query", arrayList.get(pos).getTitle());
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(context, context.getString(R.string.youtube_not_installed), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.popup_share:
                        methods.shareSong(arrayList.get(pos), true);
                        break;
                    case R.id.popup_download:
                        methods.download(arrayList.get(pos));
                        break;
                }
                return true;
            }
        });
        popup.show();
    }
}