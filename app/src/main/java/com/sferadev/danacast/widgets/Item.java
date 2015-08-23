package com.sferadev.danacast.widgets;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sferadev.danacast.R;

public class Item extends LinearLayout {

    private final ImageView mIconView;
    private final TextView mTitleView;
    private final ColorStateList mDefaultColors;
    private final int mPressedColor;
    private final int mIconActiveColor;
    private OnItemClickListener mItemClickListener;

    public Item(final Context context, AttributeSet attrs) {
        super(context, attrs);

        String title = null;
        Drawable icon = null;

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.Item);

        CharSequence sequence = array.getString(R.styleable.Item_itemTitle);
        if (sequence != null) {
            title = sequence.toString();
        }
        Drawable drawable = array.getDrawable(R.styleable.Item_itemIcon);
        if (drawable != null) {
            icon = drawable;
        }

        array.recycle();

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item, this, true);

        mTitleView = (TextView) view.findViewById(R.id.title);
        mTitleView.setText(title);
        mDefaultColors = mTitleView.getTextColors();
        mPressedColor = context.getResources().getColor(R.color.item_pressed);
        mIconActiveColor = context.getResources().getColor(R.color.colorPrimary);
        if (icon != null) {
            icon.setColorFilter(mIconActiveColor, PorterDuff.Mode.SRC_ATOP);
        }

        mIconView = (ImageView) view.findViewById(R.id.icon);
        mIconView.setImageDrawable(icon);

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (!isEnabled()) {
                    return true;
                }

                Rect mViewRect = new Rect(view.getLeft(), view.getTop(), view.getRight(),
                        view.getBottom());
                boolean mTouchCancelled = !mViewRect.contains(view.getLeft() + (int) event.getX(),
                        view.getTop() + (int) event.getY());

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        setBackgroundColor(mPressedColor);
                        break;
                    case MotionEvent.ACTION_UP:
                        setBackgroundColor(context.getResources().getColor(
                                android.R.color.transparent));
                        mTitleView.setTextColor(mDefaultColors);
                        if (mItemClickListener != null && !mTouchCancelled) {
                            mItemClickListener.onClick();
                        }
                        break;
                }
                return true;
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public void setTitle(int resourceId) {
        mTitleView.setText(resourceId);
    }

    public void setTitle(String text) {
        mTitleView.setText(text);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (mIconView != null && mTitleView != null) {
            Drawable icon = mIconView.getDrawable();
            if (enabled) {
                icon.setColorFilter(mIconActiveColor, PorterDuff.Mode.SRC_ATOP);
                mTitleView.setTextColor(mDefaultColors);
            } else {
                icon.clearColorFilter();
                mTitleView.setTextColor(getResources().getColor(R.color.card_text));
            }
        }
    }

    public interface OnItemClickListener {
        void onClick();
    }
}