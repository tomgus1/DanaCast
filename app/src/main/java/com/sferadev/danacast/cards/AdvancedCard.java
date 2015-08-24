package com.sferadev.danacast.cards;

import android.content.Context;
import android.view.View;

import com.sferadev.danacast.R;
import com.sferadev.danacast.model.EntryModel;
import com.sferadev.danacast.utils.NetworkUtils;
import com.sferadev.danacast.widgets.Card;
import com.sferadev.danacast.widgets.Item;

public class AdvancedCard extends Card {

    public AdvancedCard(final Context context, final EntryModel feed) {
        super(context);

        setTitle("Loading");
        setLayoutId(R.layout.card_advanced);

        setTitle(feed.getTitle());

        /*TextView description = (TextView) findLayoutViewById(R.id.description);
        description.setText(feed.getDescription());*/

        Item button = (Item) findLayoutViewById(R.id.button);
        if (feed.getLink() != null) {
            button.setTitle(feed.getLink());
            button.setOnItemClickListener(new Item.OnItemClickListener() {
                @Override
                public void onClick() {
                    NetworkUtils.openChromeTab(context, feed.getLink());
                }
            });
        } else {
            findLayoutViewById(R.id.separator).setVisibility(View.GONE);
            button.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean canExpand() {
        return false;
    }
}