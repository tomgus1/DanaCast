package com.sferadev.danacast.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sferadev.danacast.R;

public abstract class Card extends LinearLayout {

    private final Context mContext;
    private final LinearLayout mCardLayout;
    private final TextView mTitleView;
    private final ImageView mButton;
    private View mLayoutView;
    private boolean mExpanded = false;

    protected Card(Context context) {
        super(context);

        mContext = context;

        String title = null;

        TypedArray a = context.obtainStyledAttributes(R.styleable.Card);

        CharSequence s = a.getString(R.styleable.Card_cardTitle);
        if (s != null) {
            title = s.toString();
        }

        a.recycle();

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.card, this, true);

        mCardLayout = (LinearLayout) mView.findViewById(R.id.card_layout);
        mButton = (ImageView) mView.findViewById(R.id.headerbutton);

        if (canExpand()) {
            mButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (mExpanded) {
                        collapse();
                    } else {
                        expand();
                    }
                }

            });
        } else {
            mButton.setVisibility(View.GONE);
        }

        mTitleView = (TextView) mView.findViewById(R.id.title);
        mTitleView.setText(title);
    }

    protected void expand() {
        mExpanded = true;
        mButton.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_collapse));
    }

    protected void collapse() {
        mExpanded = false;
        mButton.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_expand));
    }

    protected boolean canExpand() {
        return true;
    }

    protected boolean isExpanded() {
        return mExpanded;
    }

    public void setTitle(int resourceId) {
        mTitleView.setText(resourceId);
    }

    protected void setTitle(String text) {
        mTitleView.setText(text);
    }

    protected void setLayoutId(int id) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayoutView = inflater.inflate(id, mCardLayout, true);
    }

    protected View findLayoutViewById(int id) {
        return mLayoutView.findViewById(id);
    }
}