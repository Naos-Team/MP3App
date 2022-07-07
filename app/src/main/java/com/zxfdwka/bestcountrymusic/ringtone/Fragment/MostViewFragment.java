package com.zxfdwka.bestcountrymusic.ringtone.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zxfdwka.bestcountrymusic.BuildConfig;
import com.zxfdwka.bestcountrymusic.R;
import com.zxfdwka.bestcountrymusic.ringtone.Adapter.RingtoneAdapter;
import com.zxfdwka.bestcountrymusic.ringtone.EndlessRecyclerViewScroll.EndlessRecyclerViewScrollListener;
import com.zxfdwka.bestcountrymusic.ringtone.Listener.ClickListenerRecorder;
import com.zxfdwka.bestcountrymusic.ringtone.Listener.InterAdListener;
import com.zxfdwka.bestcountrymusic.ringtone.Listener.RingtoneListener;
import com.zxfdwka.bestcountrymusic.ringtone.Load.LoadSongs;
import com.zxfdwka.bestcountrymusic.ringtone.Method.Methods;
import com.zxfdwka.bestcountrymusic.ringtone.SharedPref.Setting;
import com.zxfdwka.bestcountrymusic.ringtone.item.ItemRingtone;

import java.util.ArrayList;



public class MostViewFragment extends Fragment {
    Methods methods;
    RecyclerView recyclerView;
    public static RingtoneAdapter adapter;
    ArrayList<ItemRingtone> arrayList;
    ProgressBar progressBar;
    Boolean isOver = false, isScroll = false;
    int page = 1;
    GridLayoutManager grid;
    LoadSongs load;

    public View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_fragment_ringtone, container, false);

        arrayList = new ArrayList<>();
        progressBar = view.findViewById(R.id.load_video);

        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        grid = new GridLayoutManager(getActivity(), 1);
        grid.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.isHeader(position) ? grid.getSpanCount() : 1;
            }
        });
        recyclerView.setLayoutManager(grid);

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(grid) {
            @Override
            public void onLoadMore(int p, int totalItemsCount) {
                if(!isOver) {
                    try {
                        adapter.hideHeader();
                    }catch (Exception e) {
                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                } else {
                    adapter.hideHeader();
                }
            }
        });

        methods = new Methods(getActivity(), new InterAdListener() {
            @Override
            public void onClick(int position, String type) {
                adapter.notifyDataSetChanged();
            }
        });

        getData();



        return view;
    }

    private void getData() {
        load = new LoadSongs(new RingtoneListener() {
            @Override
            public void onStart() {
                if(arrayList.size() == 0) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onEnd(String success, ArrayList<ItemRingtone> arrayListWall) {
                if(arrayListWall.size() == 0) {
                    isOver = true;
                    try {
                        adapter.hideHeader();
                    }catch (Exception e) {
                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                } else {
                    page = page + 1;
                    arrayList.addAll(arrayListWall);
                    progressBar.setVisibility(View.INVISIBLE);

                    Setting.arrayList_play_rc.clear();
                    Setting.arrayList_play_rc.addAll(arrayList);
                    Setting.playPos_rc= 0;

                    Setad();
                }
            }
        },methods.getAPIRequest(Setting.METHOD_MOST_VIEWED, page, "", "", "", "", "", "", "", "","","","","","","","", null));
        load.execute();
    }

    private void Setad() {
        if(!isScroll) {
            adapter = new RingtoneAdapter(getActivity(), arrayList , new ClickListenerRecorder(){
                @Override
                public void onClick(int position) {
                    methods.showInter(position, "");
                    Setting.arrayList_play_rc.clear();
                    Setting.arrayList_play_rc.addAll(arrayList);
                    Setting.playPos_rc = position;
                    adapter.notifyDataSetChanged();
                }
            });
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }


}