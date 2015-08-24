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

import com.google.android.libraries.cast.companionlibrary.cast.BaseCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.google.android.libraries.cast.companionlibrary.widgets.MiniController;
import com.sferadev.danacast.R;
import com.sferadev.danacast.model.EntryModel;
import com.sferadev.danacast.providers.Seriesblanco;
import com.sferadev.danacast.utils.ContentUtils;

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
                updateListview(Seriesblanco.getPopularContent());
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
            case ContentUtils.TYPE_SHOW:
                updateListview(Seriesblanco.getEpisodeList(mArrayList.get(position).getLink()));
                break;
            case ContentUtils.TYPE_EPISODE:
                updateListview(Seriesblanco.getEpisodeLinks(mArrayList.get(position).getLink()));
                break;
            case ContentUtils.TYPE_LINK:
                ContentUtils.loadIntentDialog(this, Seriesblanco.getExternalLink(mArrayList.get(position).getLink()));
                break;
        }
    }

    private void updateListview(ArrayList<EntryModel> entries) {
        mArrayList = entries;
        List<String> items = new ArrayList<>();
        for (EntryModel object : mArrayList) items.add(object.getTitle());
        mListView.setSelectionAfterHeaderView();
        mAdapter.clear();
        mAdapter.addAll(items);
        mAdapter.notifyDataSetChanged();
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            mArrayList = Seriesblanco.getSearchResults(query);
        } else {
            mArrayList = Seriesblanco.getPopularContent();
        }
    }
}
