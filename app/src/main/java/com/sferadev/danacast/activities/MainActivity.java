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
import android.widget.Toast;

import com.github.snowdream.android.app.AbstractUpdateListener;
import com.github.snowdream.android.app.DownloadTask;
import com.github.snowdream.android.app.UpdateFormat;
import com.github.snowdream.android.app.UpdateInfo;
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

    private VideoCastManager mCastManager;
    private MiniController mMini;
    private SwipeRefreshLayout mRefresh;
    private SearchView searchView;

    private ArrayList<ArrayList<EntryModel>> mContent = new ArrayList<>();
    private ArrayAdapter mAdapter;

    private String LAST_CONTENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseCastManager.checkGooglePlayServices(this);
        setContentView(R.layout.activity_main);

        mCastManager = VideoCastManager.getInstance();
        mCastManager.reconnectSessionIfPossible();
        mMini = (MiniController) findViewById(R.id.miniController);
        mCastManager.addMiniController(mMini);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRefresh = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);

        mRefresh.setColorSchemeResources(R.color.colorAccent);
        mRefresh.setOnRefreshListener(this);

        ListView mListView = (ListView) findViewById(R.id.listview);

        mListView.setOnItemClickListener(this);

        handleIntent(getIntent());

        List<String> items = new ArrayList<>();
        for (EntryModel object : mContent.get(mContent.size() - 1))
            items.add(object.getTitle());
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
                mContent.add(Provider.getProviders());
                updateListview();
            }
        }, 2500);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        mCastManager.addMediaRouterButton(menu, R.id.media_route_menu_item);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = null;
        if (searchItem != null) searchView = (SearchView) searchItem.getActionView();
        if (searchView != null)
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        EntryModel entry = mContent.get(mContent.size() - 1).get(position);
        switch (entry.getType()) {
            case ContentUtils.TYPE_PROVIDER:
                PreferenceUtils.setPreference(this, PreferenceUtils.PROPERTY_LAST_PROVIDER, position);
                mContent.add(Provider.getPopularContent(this, getProvider()));
                updateListview();
                break;
            case ContentUtils.TYPE_SHOW:
                ArrayList<EntryModel> episodes = Provider.getEpisodeList(this, getProvider(), entry.getLink());
                if (!episodes.isEmpty()) {
                    LAST_CONTENT = entry.getTitle();
                    mContent.add(episodes);
                    updateListview();
                }
                break;
            case ContentUtils.TYPE_EPISODE:
                ArrayList<EntryModel> episodeLinks = Provider.getEpisodeLinks(this, getProvider(), entry.getLink());
                if (!episodeLinks.isEmpty()) {
                    LAST_CONTENT = LAST_CONTENT + " " + entry.getTitle();
                    mContent.add(episodeLinks);
                    updateListview();
                }
                break;
            case ContentUtils.TYPE_MOVIE:
                ArrayList<EntryModel> movieLinks = Provider.getMovieLinks(this, getProvider(), entry.getLink());
                if (!movieLinks.isEmpty()) {
                    LAST_CONTENT = entry.getTitle();
                    mContent.add(movieLinks);
                    updateListview();
                }
                break;
            case ContentUtils.TYPE_SONG:
                LAST_CONTENT = entry.getTitle();
            case ContentUtils.TYPE_LINK:
                ContentUtils.loadIntentDialog(this, LAST_CONTENT, entry, Provider.getExternalLink(this, getProvider(),
                        entry.getLink()));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
        } else {
            mContent.trimToSize();
            if (mContent.size() > 1) {
                mContent.remove(mContent.size() - 1);
                updateListview();
            } else finish();
        }
    }

    @SuppressWarnings("unchecked")
    private void updateListview() {
        List<String> items = new ArrayList<>();
        for (EntryModel object : mContent.get(mContent.size() - 1)) items.add(object.getTitle());
        mAdapter.clear();
        mAdapter.addAll(items);
        mAdapter.notifyDataSetChanged();
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            mContent.add(Provider.getSearchResults(this, getProvider(), query));
            updateListview();
        } else {
            mContent.add(Provider.getProviders());
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
        manager.check(this, options, new AbstractUpdateListener() {
            @Override
            public void onShowUpdateUI(UpdateInfo updateInfo) {
                Toast.makeText(MainActivity.this, "Downloading DanaCast update", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onShowNoUpdateUI() {
                //no-op
            }

            @Override
            public void onShowUpdateProgressUI(UpdateInfo updateInfo, DownloadTask downloadTask, int i) {
                //no-op
            }

            @Override
            public void ExitApp() {
                //no-op
            }
        });
    }
}
