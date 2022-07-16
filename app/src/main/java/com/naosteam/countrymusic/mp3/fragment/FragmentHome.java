package com.naosteam.countrymusic.mp3.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.naosteam.countrymusic.mp3.adapter.AdapterAlbumsHome;
import com.naosteam.countrymusic.mp3.adapter.AdapterAllSongList;
import com.naosteam.countrymusic.mp3.adapter.AdapterAppsHome;
import com.naosteam.countrymusic.mp3.adapter.AdapterArtistHome;
import com.naosteam.countrymusic.mp3.adapter.AdapterCountryHome;
import com.naosteam.countrymusic.mp3.adapter.AdapterGenresHome;
import com.naosteam.countrymusic.mp3.adapter.AdapterPlaylistHome;
import com.naosteam.countrymusic.mp3.adapter.AdapterRecent;
import com.naosteam.countrymusic.mp3.adapter.HomePagerAdapter;
import com.naosteam.countrymusic.mp3.asyncTask.LoadHome;
import com.naosteam.countrymusic.mp3.activity.ArtistByGenreActivity;
import com.naosteam.countrymusic.mp3.activity.BaseActivity;
import com.naosteam.countrymusic.mp3.activity.MainActivity;
import com.naosteam.countrymusic.mp3.activity.PlayerService;
import com.naosteam.countrymusic.R;
import com.naosteam.countrymusic.mp3.activity.SongByCatActivity;
import com.naosteam.countrymusic.mp3.interfaces.ClickListenerPlayList;
import com.naosteam.countrymusic.mp3.interfaces.HomeListener;
import com.naosteam.countrymusic.mp3.interfaces.InterAdListener;
import com.naosteam.countrymusic.mp3.item.ItemAlbums;
import com.naosteam.countrymusic.mp3.item.ItemApps;
import com.naosteam.countrymusic.mp3.item.ItemArtist;
import com.naosteam.countrymusic.mp3.item.ItemCountry;
import com.naosteam.countrymusic.mp3.item.ItemGenres;
import com.naosteam.countrymusic.mp3.item.ItemHomeBanner;
import com.naosteam.countrymusic.mp3.item.ItemServerPlayList;
import com.naosteam.countrymusic.mp3.item.ItemSong;
import com.naosteam.countrymusic.mp3.utils.Constant;
import com.naosteam.countrymusic.mp3.utils.DBHelper;
import com.naosteam.countrymusic.mp3.utils.GlobalBus;
import com.naosteam.countrymusic.mp3.utils.Methods;
import com.naosteam.countrymusic.mp3.utils.RecyclerItemClickListener;
import com.tiagosantos.enchantedviewpager.EnchantedViewPager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class FragmentHome extends Fragment {

    private DBHelper dbHelper;
    private Methods methods;
    private EnchantedViewPager enchantedViewPager;
    private HomePagerAdapter homePagerAdapter;
    private RecyclerView rv_artist, rv_songs, rv_albums, rv_recent, rv_playlist, rv_country, rv_genres, rv_apps;
    private ArrayList<ItemSong> arrayList_recent;
    private ArrayList<ItemCountry> arrayList_country;
    private ArrayList<ItemHomeBanner> arrayList_banner;
    private ArrayList<ItemArtist> arrayList_artist;
    private ArrayList<ItemGenres> arrayList_genres;
    private ArrayList<ItemAlbums> arrayList_albums;
    private ArrayList<ItemApps> arrayList_apps;
    private ArrayList<ItemSong> arrayList_trend_songs;
    private ArrayList<ItemServerPlayList> arrayList_playlist;
    private AdapterRecent adapterRecent;
    private AdapterAllSongList adapterTrending;
    private AdapterAppsHome adapterAppsHome;
    private AdapterArtistHome adapterArtistHome;
    private AdapterGenresHome adapterGenresHome;
    private AdapterAlbumsHome adapterAlbumsHome;
    private AdapterPlaylistHome adapterPlaylistHome;
    private AdapterCountryHome adapterCountry;
    private CircularProgressBar progressBar;
    private FrameLayout frameLayout;
    private String addedFrom = "";
    private String errr_msg;

    private LinearLayout ll_trending, ll_artist, ll_albums, ll_recent, ll_playlist, ll_country, ll_genres, ll_apps, ll_home_banner;
    private TextView tv_songs_all, tv_recent_all, tv_albums_all, tv_artist_all, tv_playlist_all, tv_country_all, tv_genres_all, tv_apps_all;
    private LinearLayout ll_home;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);

        methods = new Methods(getActivity(), new InterAdListener() {
            @Override
            public void onClick(int position, String type) {
                if (type.equals(getString(R.string.songs))) {
                    Intent intent = new Intent(getActivity(), PlayerService.class);
                    intent.setAction(PlayerService.ACTION_PLAY);
                    getActivity().startService(intent);
                } else if (type.equals(getString(R.string.recent))) {
                    Intent intent = new Intent(getActivity(), PlayerService.class);
                    intent.setAction(PlayerService.ACTION_PLAY);
                    getActivity().startService(intent);
                } else if (type.equals(getString(R.string.artist))) {

                    FragmentAlbumsByArtist f_all_songs = new FragmentAlbumsByArtist();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("item", arrayList_artist.get(position));
                    f_all_songs.setArguments(bundle);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.hide(getFragmentManager().getFragments().get(getFragmentManager().getBackStackEntryCount()));
                    ft.add(R.id.fragment, f_all_songs, getString(R.string.albums));
                    ft.addToBackStack(getString(R.string.albums));
                    ft.commitAllowingStateLoss();
                    ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.albums));

                } else if (type.equals(getString(R.string.playlist))) {

                    Intent intent = new Intent(getActivity(), SongByCatActivity.class);
                    intent.putExtra("type", getString(R.string.playlist));
                    intent.putExtra("id", arrayList_playlist.get(position).getId());
                    intent.putExtra("name", arrayList_playlist.get(position).getName());
                    intent.putExtra("image", arrayList_playlist.get(position).getImage());
                    startActivity(intent);

                } else if (type.equals(getString(R.string.albums))) {
                    Intent intent = new Intent(getActivity(), SongByCatActivity.class);
                    intent.putExtra("type", getString(R.string.albums));
                    intent.putExtra("id", arrayList_albums.get(position).getId());
                    intent.putExtra("name", arrayList_albums.get(position).getName());
                    intent.putExtra("image", arrayList_albums.get(position).getImage());
                    startActivity(intent);
                } else if (type.equals(getString(R.string.countries))) {
                    Intent intent = new Intent(getActivity(), SongByCatActivity.class);
                    intent.putExtra("type", getString(R.string.countries));
                    intent.putExtra("id", arrayList_country.get(position).getId());
                    intent.putExtra("name", arrayList_country.get(position).getName());
                    intent.putExtra("image", arrayList_country.get(position).getImage());
                    startActivity(intent);
                }  else if (type.equals(getString(R.string.genres))) {
                    Intent intent = new Intent(getActivity(), ArtistByGenreActivity.class);
                    intent.putExtra("item", arrayList_genres.get(position));
                    startActivity(intent);
                } else if (type.equals(getString(R.string.banner))) {
                    Intent intent = new Intent(getActivity(), SongByCatActivity.class);
                    intent.putExtra("type", getString(R.string.banner));
                    intent.putExtra("id", arrayList_banner.get(position).getId());
                    intent.putExtra("name", arrayList_banner.get(position).getTitle());
                    intent.putExtra("songs", arrayList_banner.get(position).getArrayListSongs());
                    intent.putExtra("image", arrayList_banner.get(position).getImage());
                    startActivity(intent);
                } else if (type.equals(getString(R.string.moreapp))) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(arrayList_apps.get(position).getURL()));
                    startActivity(browserIntent);
                }
            }
        });
        dbHelper = new DBHelper(getActivity());

        arrayList_recent = new ArrayList<>();
        arrayList_artist = new ArrayList<>();
        arrayList_banner = new ArrayList<>();
        arrayList_albums = new ArrayList<>();
        arrayList_playlist = new ArrayList<>();
        arrayList_country = new ArrayList<>();
        arrayList_genres = new ArrayList<>();
        arrayList_trend_songs = new ArrayList<>();
        arrayList_apps = new ArrayList<>();

        progressBar = rootView.findViewById(R.id.pb_home);
        frameLayout = rootView.findViewById(R.id.fl_empty);

        LayoutInflater inflat = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View myView = inflat.inflate(R.layout.layout_err_internet, null);
        frameLayout.addView(myView);
        myView.findViewById(R.id.btn_empty_try).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadHome();
            }
        });

        enchantedViewPager = rootView.findViewById(R.id.viewPager_home);
        enchantedViewPager.useAlpha();
        enchantedViewPager.useScale();

        rv_artist = rootView.findViewById(R.id.rv_home_artist);
        LinearLayoutManager llm_artist = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rv_artist.setLayoutManager(llm_artist);
        rv_artist.setItemAnimator(new DefaultItemAnimator());
        rv_artist.setHasFixedSize(true);

        rv_country = rootView.findViewById(R.id.rv_home_country);
        LinearLayoutManager llm_country = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rv_country.setLayoutManager(llm_country);
        rv_country.setItemAnimator(new DefaultItemAnimator());
        rv_country.setHasFixedSize(true);

        rv_genres = rootView.findViewById(R.id.rv_home_genres);
        LinearLayoutManager llm_genres = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rv_genres.setLayoutManager(llm_genres);
        rv_genres.setItemAnimator(new DefaultItemAnimator());
        rv_genres.setHasFixedSize(true);

        rv_albums = rootView.findViewById(R.id.rv_home_albums);
        LinearLayoutManager llm_albums = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rv_albums.setLayoutManager(llm_albums);
        rv_albums.setItemAnimator(new DefaultItemAnimator());
        rv_albums.setHasFixedSize(true);

        rv_playlist = rootView.findViewById(R.id.rv_home_playlist);
        LinearLayoutManager llm_playlist = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rv_playlist.setLayoutManager(llm_playlist);
        rv_playlist.setItemAnimator(new DefaultItemAnimator());
        rv_playlist.setHasFixedSize(true);

        rv_songs = rootView.findViewById(R.id.rv_home_songs);
        LinearLayoutManager llm_songs = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rv_songs.setLayoutManager(llm_songs);
        rv_songs.setItemAnimator(new DefaultItemAnimator());
        rv_songs.setHasFixedSize(true);

        rv_recent = rootView.findViewById(R.id.rv_home_recent);
        LinearLayoutManager llm_recent = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rv_recent.setLayoutManager(llm_recent);
        rv_recent.setItemAnimator(new DefaultItemAnimator());
        rv_recent.setHasFixedSize(true);

        rv_apps = rootView.findViewById(R.id.rv_home_apps);
        LinearLayoutManager llm_apps = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rv_apps.setLayoutManager(llm_apps);
        rv_apps.setItemAnimator(new DefaultItemAnimator());
        rv_apps.setHasFixedSize(true);

        ll_home = rootView.findViewById(R.id.ll_home);

        ll_trending = rootView.findViewById(R.id.ll_trending);
        ll_artist = rootView.findViewById(R.id.ll_artist);
        ll_albums = rootView.findViewById(R.id.ll_albums);
        ll_recent = rootView.findViewById(R.id.ll_recent);
        ll_playlist = rootView.findViewById(R.id.ll_playlist);
        ll_country = rootView.findViewById(R.id.ll_country);
        ll_genres = rootView.findViewById(R.id.ll_genres);
        ll_apps = rootView.findViewById(R.id.ll_apps);
        ll_home_banner = rootView.findViewById(R.id.ll_home_banner);

        tv_country_all = rootView.findViewById(R.id.tv_home_country_all);
        tv_genres_all = rootView.findViewById(R.id.tv_home_genres_all);
        tv_artist_all = rootView.findViewById(R.id.tv_home_artist_all);
        tv_songs_all = rootView.findViewById(R.id.tv_home_songs_all);
        tv_albums_all = rootView.findViewById(R.id.tv_home_albums_all);
        tv_recent_all = rootView.findViewById(R.id.tv_home_recent_all);
        tv_playlist_all = rootView.findViewById(R.id.tv_home_plalist_all);
        tv_apps_all = rootView.findViewById(R.id.tv_home_apps_all);

        loadHome();

        rv_artist.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                methods.showInterAd(position, getString(R.string.artist));
            }
        }));

        rv_country.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                methods.showInterAd(position, getString(R.string.countries));
            }
        }));

        rv_genres.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                methods.showInterAd(position, getString(R.string.genres));
            }
        }));

        rv_albums.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                methods.showInterAd(position, getString(R.string.albums));
            }
        }));

        rv_playlist.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                methods.showInterAd(position, getString(R.string.playlist));
            }
        }));

        rv_apps.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                methods.showInterAd(position, getString(R.string.moreapp));
            }
        }));

        tv_artist_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentArtist f_art = new FragmentArtist();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.hide(getFragmentManager().getFragments().get(getFragmentManager().getBackStackEntryCount()));
                ft.add(R.id.fragment, f_art, getString(R.string.artist));
                ft.addToBackStack(getString(R.string.artist));
                ft.commitAllowingStateLoss();
                ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.artist));
            }
        });

        tv_country_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentCountries f_country = new FragmentCountries();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.hide(getFragmentManager().getFragments().get(getFragmentManager().getBackStackEntryCount()));
                ft.add(R.id.fragment, f_country, getString(R.string.countries));
                ft.addToBackStack(getString(R.string.countries));
                ft.commitAllowingStateLoss();
                ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.countries));
            }
        });

        tv_genres_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentGenres f_genres = new FragmentGenres();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.hide(getFragmentManager().getFragments().get(getFragmentManager().getBackStackEntryCount()));
                ft.add(R.id.fragment, f_genres, getString(R.string.genres));
                ft.addToBackStack(getString(R.string.genres));
                ft.commitAllowingStateLoss();
                ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.genres));
            }
        });

        tv_albums_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentAlbums f_albums = new FragmentAlbums();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.hide(getFragmentManager().getFragments().get(getFragmentManager().getBackStackEntryCount()));
                ft.add(R.id.fragment, f_albums, getString(R.string.albums));
                ft.addToBackStack(getString(R.string.albums));
                ft.commitAllowingStateLoss();
                ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.albums));
            }
        });

        tv_playlist_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentServerPlaylist f_playlist = new FragmentServerPlaylist();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.hide(getFragmentManager().getFragments().get(getFragmentManager().getBackStackEntryCount()));
                ft.add(R.id.fragment, f_playlist, getString(R.string.playlist));
                ft.addToBackStack(getString(R.string.playlist));
                ft.commitAllowingStateLoss();
                ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.playlist));
            }
        });

        tv_songs_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTrendingSongs f_all_songs = new FragmentTrendingSongs();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.hide(getFragmentManager().getFragments().get(getFragmentManager().getBackStackEntryCount()));
                ft.add(R.id.fragment, f_all_songs, getString(R.string.trending_songs));
                ft.addToBackStack(getString(R.string.trending_songs));
                ft.commitAllowingStateLoss();
                ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.trending_songs));
            }
        });

        tv_apps_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentApps f_apps = new FragmentApps();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.hide(getFragmentManager().getFragments().get(getFragmentManager().getBackStackEntryCount()));
                ft.add(R.id.fragment, f_apps, getString(R.string.apps));
                ft.addToBackStack(getString(R.string.apps));
                ft.commitAllowingStateLoss();
                ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.apps));
            }
        });

        tv_recent_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FragmentDashBoard.bottomNavigation.setCurrentItem(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        methods.showSMARTBannerAd(ll_home_banner);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem item = menu.findItem(R.id.menu_search);
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setOnQueryTextListener(queryTextListener);
        super.onCreateOptionsMenu(menu, inflater);
    }

    SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            Constant.search_item = s.replace(" ", "%20");
            FragmentSearch fsearch = new FragmentSearch();
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.hide(fm.findFragmentByTag(getString(R.string.home)));
            ft.add(R.id.fragment, fsearch, getString(R.string.search));
            ft.addToBackStack(getString(R.string.search));
            ft.commitAllowingStateLoss();
            return true;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            return false;
        }
    };

    private void loadHome() {
        if (methods.isNetworkAvailable()) {
            LoadHome loadHome = new LoadHome(new HomeListener() {
                @Override
                public void onStart() {
                    frameLayout.setVisibility(View.GONE);
                    ll_home.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onEnd(String success, ArrayList<ItemHomeBanner> arrayListBanner, ArrayList<ItemAlbums> arrayListAlbums, ArrayList<ItemArtist> arrayListArtist,  ArrayList<ItemServerPlayList> arrayListPlaylist, ArrayList<ItemSong> arrayListSongs, ArrayList<ItemCountry> arrayListCountry, ArrayList<ItemGenres> arrayListGenres, ArrayList<ItemApps> arrayListApp) {
                    if (getActivity() != null) {
                        if (success.equals("1")) {
                            arrayList_apps.addAll(arrayListApp);
                            arrayList_banner.addAll(arrayListBanner);
                            arrayList_albums.addAll(arrayListAlbums);
                            arrayList_artist.addAll(arrayListArtist);
                            arrayList_country.addAll(arrayListCountry);
                            arrayList_genres.addAll(arrayListGenres);
                            arrayList_playlist.addAll(arrayListPlaylist);
                            arrayList_trend_songs.addAll(arrayListSongs);
                            arrayList_recent.addAll(dbHelper.loadDataRecent(true, "15"));

                            if (Constant.arrayList_play.size() == 0 && arrayListSongs.size() > 0) {
                                Constant.arrayList_play.addAll(arrayListSongs);
                                ((BaseActivity) getActivity()).changeText(Constant.arrayList_play.get(0), "home");
                            }

                            loadEmpty();

                            homePagerAdapter = new HomePagerAdapter(getActivity(), arrayList_banner);
                            enchantedViewPager.setAdapter(homePagerAdapter);
                            if (homePagerAdapter.getCount() > 2) {
                                enchantedViewPager.setCurrentItem(1);
                            }

                            adapterArtistHome = new AdapterArtistHome(arrayList_artist, getActivity());
                            rv_artist.setAdapter(adapterArtistHome);

                            adapterCountry = new AdapterCountryHome(arrayList_country, getActivity());
                            rv_country.setAdapter(adapterCountry);

                            adapterGenresHome = new AdapterGenresHome(arrayList_genres, getActivity());
                            rv_genres.setAdapter(adapterGenresHome);

                            adapterTrending = new AdapterAllSongList(getActivity(), arrayList_trend_songs, new ClickListenerPlayList() {
                                @Override
                                public void onClick(int position) {
                                    if (methods.isNetworkAvailable()) {
                                        Constant.isOnline = true;
                                        addedFrom = "trend";
                                        if (!Constant.addedFrom.equals(addedFrom)) {
                                            Constant.arrayList_play.clear();
                                            Constant.arrayList_play.addAll(arrayList_trend_songs);
                                            Constant.addedFrom = addedFrom;
                                            Constant.isNewAdded = true;
                                        }
                                        Constant.playPos = position;

                                        methods.showInterAd(position, getString(R.string.songs));
                                    } else {
                                        Toast.makeText(getActivity(), getResources().getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onItemZero() {

                                }
                            }, "online");
                            rv_songs.setAdapter(adapterTrending);

                            adapterRecent = new AdapterRecent(getActivity(), arrayList_recent, new ClickListenerPlayList() {
                                @Override
                                public void onClick(int position) {
                                    if (methods.isNetworkAvailable()) {
                                        addedFrom = "recent";
                                        Constant.isOnline = true;
                                        if (!Constant.addedFrom.equals(addedFrom)) {
                                            Constant.arrayList_play.clear();
                                            Constant.arrayList_play.addAll(arrayList_recent);
                                            Constant.addedFrom = addedFrom;
                                            Constant.isNewAdded = true;
                                        }
                                        Constant.playPos = position;

                                        methods.showInterAd(position, getString(R.string.recent));
                                    } else {
                                        Toast.makeText(getActivity(), getResources().getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onItemZero() {

                                }
                            });
                            rv_recent.setAdapter(adapterRecent);

                            adapterAlbumsHome = new AdapterAlbumsHome(arrayList_albums, getActivity());
                            rv_albums.setAdapter(adapterAlbumsHome);

                            adapterPlaylistHome = new AdapterPlaylistHome(arrayList_playlist, getActivity());
                            rv_playlist.setAdapter(adapterPlaylistHome);

                            adapterAppsHome = new AdapterAppsHome(arrayList_apps, getActivity());
                            rv_apps.setAdapter(adapterAppsHome);

                            ll_home.setVisibility(View.VISIBLE);
                            errr_msg = getString(R.string.err_no_artist_found);
                        } else {
                            errr_msg = getString(R.string.err_server);
                        }

                        progressBar.setVisibility(View.GONE);
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_HOME, 0,"","","","","","","","","","","","","",Constant.itemUser.getId(),"", null));
            loadHome.execute();
        } else {
            errr_msg = getString(R.string.err_internet_not_conn);
            frameLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void loadEmpty() {

        if (arrayList_recent.size() < 3) {
            ll_recent.setVisibility(View.GONE);
        } else {
            ll_recent.setVisibility(View.VISIBLE);
        }

        if (arrayList_trend_songs.size() == 0) {
            ll_trending.setVisibility(View.GONE);
        } else {
            ll_trending.setVisibility(View.VISIBLE);
        }

        if (arrayList_artist.size() == 0) {
            ll_artist.setVisibility(View.GONE);
        } else {
            ll_artist.setVisibility(View.VISIBLE);
        }

        if (arrayList_country.size() == 0) {
            ll_country.setVisibility(View.GONE);
        } else {
            ll_country.setVisibility(View.VISIBLE);
        }

        if (arrayList_genres.size() == 0) {
            ll_genres.setVisibility(View.GONE);
        } else {
            ll_genres.setVisibility(View.VISIBLE);
        }

        if (arrayList_albums.size() == 0) {
            ll_albums.setVisibility(View.GONE);
        } else {
            ll_albums.setVisibility(View.VISIBLE);
        }

        if (arrayList_apps.size() == 0) {
            ll_apps.setVisibility(View.GONE);
        } else {
            ll_apps.setVisibility(View.VISIBLE);
        }

        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    adapterTrending.hideHeader();
                }
            }, 2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEquilizerChange(ItemAlbums itemAlbums) {
        if(adapterTrending != null) {
            adapterTrending.notifyDataSetChanged();
        }
        GlobalBus.getBus().removeStickyEvent(itemAlbums);
    }

    @Override
    public void onStart() {
        super.onStart();
        GlobalBus.getBus().register(this);
    }

    @Override
    public void onStop() {
        GlobalBus.getBus().unregister(this);
        super.onStop();
    }
}