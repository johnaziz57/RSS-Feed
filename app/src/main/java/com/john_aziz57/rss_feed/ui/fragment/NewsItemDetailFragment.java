package com.john_aziz57.rss_feed.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.john_aziz57.rss_feed.R;
import com.john_aziz57.rss_feed.data.model.NewsItem;
import com.john_aziz57.rss_feed.ui.activity.NewsItemDetailActivity;
import com.john_aziz57.rss_feed.ui.activity.NewsItemListActivity;
import com.john_aziz57.rss_feed.utils.Utils;

/**
 * A fragment representing a single NewsItem detail screen.
 * This fragment is either contained in a {@link NewsItemListActivity}
 * in two-pane mode (on tablets) or a {@link NewsItemDetailActivity}
 * on handsets.
 */
public class NewsItemDetailFragment extends Fragment {

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The newsItem that the fragment represents
     */
    private NewsItem mNewsItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NewsItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {

            mNewsItem = getArguments().getParcelable(ARG_ITEM_ID);

            NewsItemDetailActivity activity = (NewsItemDetailActivity)this.getActivity();
            ActionBar toolbar = activity.getSupportActionBar();
            if (toolbar!= null) {
                toolbar.setTitle(mNewsItem.title);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.newsitem_detail, container, false);

        if (mNewsItem != null) {
            ((TextView) rootView.findViewById(R.id.title_textview)).setText(mNewsItem.title);
            ((TextView) rootView.findViewById(R.id.description_textview)).setText(mNewsItem.description);

            if(mNewsItem.thumbnail!=null) {
                ImageView imageView = (ImageView) rootView.findViewById(R.id.media_imageView);
                Glide
                        .with(this)
                        .load(mNewsItem.thumbnail.url)
                        .placeholder(R.drawable.bbc_placeholder)
                        .into(imageView);
            }

            TextView link = (TextView) rootView.findViewById(R.id.link_textview);
            link.setText(Utils.getTheMoreLink(mNewsItem.link));
            link.setMovementMethod(LinkMovementMethod.getInstance());
        }


        return rootView;
    }
}
