package com.naosteam.countrymusic.mp3.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.naosteam.countrymusic.mp3.adapter.AdapterMyPlaylist;
import com.naosteam.countrymusic.mp3.interfaces.ClickListenerPlayList;
import com.naosteam.countrymusic.mp3.interfaces.InterAdListener;
import com.naosteam.countrymusic.mp3.item.ItemMyPlayList;
import com.naosteam.countrymusic.R;
import com.naosteam.countrymusic.mp3.activity.SongByOFFPlaylistActivity;
import com.naosteam.countrymusic.mp3.utils.DBHelper;
import com.naosteam.countrymusic.mp3.utils.Methods;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class FragmentOFPlaylist extends Fragment {

    private DBHelper dbHelper;
    private Methods methods;
    private RecyclerView rv;
    private Button button_add;
    private AdapterMyPlaylist adapterMyPlaylist;
    private ArrayList<ItemMyPlayList> arrayList;
    private FrameLayout frameLayout;
    private Boolean isLoaded = false;
    private SearchView searchView;

    public static FragmentOFPlaylist newInstance(int sectionNumber) {
        return new FragmentOFPlaylist();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_my_playlist, container, false);

        dbHelper = new DBHelper(getActivity());

        methods = new Methods(getActivity(), new InterAdListener() {
            @Override
            public void onClick(int position, String type) {
                Intent intent = new Intent(getActivity(), SongByOFFPlaylistActivity.class);
                intent.putExtra("item", adapterMyPlaylist.getItem(position));
                startActivity(intent);
            }
        });

        arrayList = new ArrayList<>();
        arrayList.addAll(dbHelper.loadPlayList(false));

        button_add = rootView.findViewById(R.id.button_add_myplaylist);
        frameLayout = rootView.findViewById(R.id.fl_empty);

        rv = rootView.findViewById(R.id.rv_myplaylist);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        rv.setLayoutManager(gridLayoutManager);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setHasFixedSize(true);
        rv.setNestedScrollingEnabled(false);

        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddPlaylistDialog();
            }
        });

        adapterMyPlaylist = new AdapterMyPlaylist(getActivity(), arrayList, new ClickListenerPlayList() {
            @Override
            public void onClick(int position) {
                methods.showInterAd(position, "");
            }

            @Override
            public void onItemZero() {
                setEmpty();
            }
        }, false);

        rv.setAdapter(adapterMyPlaylist);
        setEmpty();

        setHasOptionsMenu(true);
        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem item = menu.findItem(R.id.menu_search);
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setOnQueryTextListener(queryTextListener);
        super.onCreateOptionsMenu(menu, inflater);
    }

    SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            if (adapterMyPlaylist != null) {
                if (!searchView.isIconified()) {
                    adapterMyPlaylist.getFilter().filter(s);
                    adapterMyPlaylist.notifyDataSetChanged();
                }
            }
            return true;
        }
    };

    private void setEmpty() {
        if (arrayList.size() > 0) {
            frameLayout.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);
        } else {
            frameLayout.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);

            frameLayout.removeAllViews();
            LayoutInflater infltr = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View myView = infltr.inflate(R.layout.layout_err_nodata, null);

            TextView textView = myView.findViewById(R.id.tv_empty_msg);
            textView.setText(getString(R.string.err_no_playlist_found));

            myView.findViewById(R.id.btn_empty_try).setVisibility(View.GONE);
            frameLayout.addView(myView);
        }
    }

    private void openAddPlaylistDialog() {
        final InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_add_playlist);

        final EditText editText = dialog.findViewById(R.id.et_dialog_addplay);
        final ImageView iv_close = dialog.findViewById(R.id.iv_addplay_close);
        final Button button = dialog.findViewById(R.id.button_addplay_add);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editText.getText().toString().trim().isEmpty()) {
                    arrayList.clear();
                    arrayList.addAll(dbHelper.addPlayList(editText.getText().toString(), false));
                    Toast.makeText(getActivity(), getString(R.string.playlist_added), Toast.LENGTH_SHORT).show();
                    adapterMyPlaylist.notifyDataSetChanged();
                    setEmpty();
                    dialog.dismiss();
                }
            }
        });

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        new Handler().post(
                new Runnable() {
                    public void run() {
						editText.requestFocus();
                        inputMethodManager.showSoftInput(editText, 0);
                    }
                });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && adapterMyPlaylist != null) {
            arrayList.clear();
            arrayList.addAll(dbHelper.loadPlayList(false));
            adapterMyPlaylist.notifyDataSetChanged();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onResume() {
        if (isLoaded && adapterMyPlaylist != null) {
            arrayList.clear();
            arrayList.addAll(dbHelper.loadPlayList(false));
            adapterMyPlaylist.notifyDataSetChanged();
        } else {
            isLoaded = true;
        }
        super.onResume();
    }
}