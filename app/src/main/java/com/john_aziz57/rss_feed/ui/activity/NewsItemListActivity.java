package com.john_aziz57.rss_feed.ui.activity;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.john_aziz57.rss_feed.data.loader.RssTaskLoader;
import com.john_aziz57.rss_feed.data.model.NewsItem;
import com.john_aziz57.rss_feed.data.model.RSS;
import com.john_aziz57.rss_feed.ui.fragment.NewsItemDetailFragment;
import com.john_aziz57.rss_feed.R;

import java.util.LinkedList;
import java.util.List;

/**
 * An activity representing a list of NewsItems. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link NewsItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class NewsItemListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<RSS>{

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private static final int RSS_ID=1;

    private boolean mTwoPane;
    /*SimpleAdapter of RecyvlerView reference to dynamically add items to it and refresh the view*/
    private SimpleItemRecyclerViewAdapter mSimpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsitem_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        View recyclerView = findViewById(R.id.newsitem_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.newsitem_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
        final LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(RSS_ID,null,this).forceLoad();
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        mSimpleAdapter = new SimpleItemRecyclerViewAdapter(new LinkedList<NewsItem>());
        recyclerView.setAdapter(mSimpleAdapter);
    }

    @Override
    public Loader<RSS> onCreateLoader(int i, Bundle bundle) {
        return new RssTaskLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<RSS> loader, RSS rss) {
        //TODO handle swipe end refesh
        if(rss!=null){
            mSimpleAdapter.clear();
            mSimpleAdapter.addItems(rss);
        }
    }

    @Override
    public void onLoaderReset(Loader<RSS> loader) {

    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<NewsItem> mNewsItemList;

        public SimpleItemRecyclerViewAdapter(List<NewsItem> items) {
            mNewsItemList = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.newsitem_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final NewsItem newsItem = mNewsItemList.get(position);

            holder.mTitleView.setText(newsItem.title);
            holder.mPubDateView.setText(newsItem.pubDate);

            /*if image link does exist, load it into the circular imageView*/
            if(newsItem.thumbnail!=null)
                Glide
                        .with(NewsItemListActivity.this)
                        .load(newsItem.thumbnail.url)
                        .asBitmap()
                        .into(holder.mImageView);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putParcelable(NewsItemDetailFragment.ARG_ITEM_ID, newsItem);
                        NewsItemDetailFragment fragment = new NewsItemDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.newsitem_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, NewsItemDetailActivity.class);
                        intent.putExtra(NewsItemDetailFragment.ARG_ITEM_ID, newsItem);

                        context.startActivity(intent);
                    }
                }
            });
        }
        /*Add items directly from RSS*/
        public void addItems(RSS rss){
            if(rss!=null && rss.channel!=null)
                addItems(rss.channel.list);
        }

        /*Add items from list*/
        public void addItems(List<NewsItem> newsItemList){
            mNewsItemList.addAll(newsItemList);
            //notify changes
            notifyDataSetChanged();
        }

        /*Clear the adapter and notify the dataset */
        public void clear(){
            mNewsItemList.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return mNewsItemList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mTitleView;
            public final TextView mPubDateView;
            public final ImageView mImageView;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                /*News title textView*/
                mTitleView = (TextView) view.findViewById(R.id.title_textview);
                /*News description textView*/
                mPubDateView = (TextView) view.findViewById(R.id.pubdate_textview);
                /*News Image*/
                mImageView = (ImageView) view.findViewById(R.id.image_imageview);
            }
        }
    }
}
