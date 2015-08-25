package com.sferadev.danacast.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.snowdream.android.app.UpdateFormat;
import com.github.snowdream.android.app.UpdateManager;
import com.github.snowdream.android.app.UpdateOptions;
import com.github.snowdream.android.app.UpdatePeriod;
import com.google.android.libraries.cast.companionlibrary.cast.BaseCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.google.android.libraries.cast.companionlibrary.widgets.MiniController;
import com.sferadev.danacast.R;
import com.sferadev.danacast.model.EntryModel;
import com.sferadev.danacast.providers.Provider;
import com.sferadev.danacast.utils.ContentUtils;
import com.sferadev.danacast.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    VideoCastManager mCastManager;
    private SwipeRefreshLayout mRefresh;
    private MiniController mMini;

    private ListView mListView;
    private ArrayList<EntryModel> mArrayList;
    private ArrayAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseCastManager.checkGooglePlayServices(this);
        setContentView(R.layout.activity_main);

        mCastManager = VideoCastManager.getInstance();

        mMini = (MiniController) findViewById(R.id.miniController);
        mCastManager.addMiniController(mMini);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRefresh = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);

        mRefresh.setColorSchemeResources(R.color.colorAccent);
        mRefresh.setOnRefreshListener(this);

        mListView = (ListView) findViewById(R.id.listview);

        mListView.setOnItemClickListener(this);

        mCastManager.reconnectSessionIfPossible();

        handleIntent(getIntent());

        List<String> items = new ArrayList<>();
        for (EntryModel object : mArrayList) items.add(object.getTitle());
        mAdapter = new ArrayAdapter<>(
                this,
                R.layout.list_row,
                R.id.drawer_text,
                items);
        mListView.setAdapter(mAdapter);

        checkUpdates();
    }

    @Override
    public void onResume() {
        mCastManager = VideoCastManager.getInstance();
        if (null != mCastManager) {
            mCastManager.incrementUiCounter();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        mCastManager.decrementUiCounter();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (null != mCastManager) {
            mMini.removeOnMiniControllerChangedListener(mCastManager);
            mCastManager.removeMiniController(mMini);
        }
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    @Override
    public void onRefresh() {
        // Handle the refresh of mRefresh
        mRefresh.setRefreshing(true);
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                mRefresh.setRefreshing(false);
                updateListview(Provider.getProviders());
            }
        }, 2500);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        mCastManager.addMediaRouterButton(menu, R.id.media_route_menu_item);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = null;
        if (searchItem != null) searchView = (SearchView) searchItem.getActionView();
        if (searchView != null)
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (mArrayList.get(position).getType()) {
            case ContentUtils.TYPE_PROVIDER:
                PreferenceUtils.setPreference(this, PreferenceUtils.PROPERTY_LAST_PROVIDER, position);
                updateListview(Provider.getPopularContent(this, getProvider()));
                break;
            case ContentUtils.TYPE_SHOW:
                ArrayList<EntryModel> episodes = Provider.getEpisodeList(this, getProvider(), mArrayList.get(position).getLink());
                if (!episodes.isEmpty()) updateListview(episodes);
                break;
            case ContentUtils.TYPE_EPISODE:
                ArrayList<EntryModel> episodeLinks = Provider.getEpisodeLinks(this, getProvider(), mArrayList.get(position).getLink());
                if (!episodeLinks.isEmpty()) updateListview(episodeLinks);
                break;
            case ContentUtils.TYPE_MOVIE:
                ArrayList<EntryModel> movieLinks = Provider.getMovieLinks(this, getProvider(), mArrayList.get(position).getLink());
                if (!movieLinks.isEmpty()) updateListview(movieLinks);
                break;
            case ContentUtils.TYPE_LINK:
                ContentUtils.loadIntentDialog(this, Provider.getExternalLink(this, getProvider(),
                        mArrayList.get(position).getLink()));
                break;
        }
    }

    private void updateListview(ArrayList<EntryModel> entries) {
        mArrayList = entries;
        List<String> items = new ArrayList<>();
        for (EntryModel object : mArrayList) items.add(object.getTitle());
        updateListview(items);
    }

    private void updateListview(List<String> items) {
        mListView.setSelectionAfterHeaderView();
        mAdapter.clear();
        mAdapter.addAll(items);
        mAdapter.notifyDataSetChanged();
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            mArrayList = Provider.getSearchResults(this, getProvider(), query);
            updateListview(mArrayList);
        } else {
            mArrayList = Provider.getProviders();
        }
    }

    private int getProvider() {
        return PreferenceUtils.getPreference(this, PreferenceUtils.PROPERTY_LAST_PROVIDER, 0);
    }

    private void checkUpdates() {
        UpdateManager manager = new UpdateManager(this);
        UpdateOptions options = new UpdateOptions.Builder(this)
                .checkUrl("https://raw.githubusercontent.com/SferaDev/DanaCast/master/updates/update.json")
                .updateFormat(UpdateFormat.JSON)
                .updatePeriod(new UpdatePeriod(UpdatePeriod.EACH_TIME))
                .checkPackageName(true)
                .build();
        manager.check(this, options);
    }
}
