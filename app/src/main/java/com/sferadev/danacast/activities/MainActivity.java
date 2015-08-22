package com.sferadev.danacast.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.widget.LinearLayout;

import com.sferadev.danacast.R;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private LinearLayout mCardsLayout;
    private SwipeRefreshLayout mRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCardsLayout = (LinearLayout) findViewById(R.id.cards_layout);
        mRefresh = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);

        mRefresh.setColorSchemeResources(R.color.colorAccent);
        mRefresh.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        // Handle the refresh of mRefresh
        mRefresh.setRefreshing(true);
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                mRefresh.setRefreshing(false);
                //Refresh TODO
            }
        }, 2500);
    }

}
