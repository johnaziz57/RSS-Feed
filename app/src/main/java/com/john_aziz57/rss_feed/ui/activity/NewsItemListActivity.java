package com.john_aziz57.rss_feed.ui.activity;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

    /*loader ID*/
    private static final int RSS_ID=1;
    /*Current NewsItem key for restoring state*/
    private static final String CURRENT_TOPIC_KEY = "CURRENT_TOPIC";

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    /*SimpleAdapter of RecyvlerView reference to dynamically add items to it and refresh the view*/
    private SimpleItemRecyclerViewAdapter mSimpleAdapter;
    private RecyclerView mRecyclerView;
    /* When there is no internet connection use this textView to display warning*/
    private TextView mNoDataTextView;
    /*loading progress bar*/
    private ProgressBar mProgressBar;
    /*the layout holding the detail fragment during tablet view*/
    private FrameLayout mFrameLayout;
    /*the textview asking the user to select a topic during tablet view*/
    private TextView mPleaseSelectTextView;
    /*swipe to refresh view for the recyclerView*/
    private SwipeRefreshLayout mSwipeContainer;
    /*flag to indicate that a loading is started*/
    private boolean mStartedLoading;

    private NewsItem mLastSelectedNewsItem =null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsitem_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        mRecyclerView = (RecyclerView)findViewById(R.id.newsitem_list);
        assert mRecyclerView  != null;
        setupRecyclerView( mRecyclerView );
        if (findViewById(R.id.newsitem_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
            mFrameLayout = (FrameLayout) findViewById(R.id.newsitem_detail_container);
            mPleaseSelectTextView = (TextView) findViewById(R.id.please_select_textview);

            // workaround: set the view to landscape in tablet to avoid rotation problems
            //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        mNoDataTextView = (TextView) findViewById(R.id.no_data_textview);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        final LoaderManager loaderManager = getLoaderManager();
        // start the loader to fetch data
        loaderManager.initLoader(RSS_ID,null,this).forceLoad();

        //setup swipe to refresh container
        mSwipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!mStartedLoading)// not already refreshing
                // on refresh load the data
                loaderManager.restartLoader(RSS_ID,null,NewsItemListActivity.this).forceLoad();
            }
        });

        mSwipeContainer.setColorSchemeResources(R.color.colorPrimary,R.color.colorPrimaryDark,R.color.colorAccent);

        // disable swipe to refresh because we are already loading the data, don't refresh
        mSwipeContainer.setEnabled(false);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        mSimpleAdapter = new SimpleItemRecyclerViewAdapter(new LinkedList<NewsItem>());
        recyclerView.setAdapter(mSimpleAdapter);
    }

    @Override
    public Loader<RSS> onCreateLoader(int i, Bundle bundle) {
        mStartedLoading = true;
        return new RssTaskLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<RSS> loader, RSS rss) {
        // hide the initial progressbar
        mProgressBar.setVisibility(View.GONE);
        // stop the refreshing sign
        mSwipeContainer.setRefreshing(false);
        //allow swipe to refresh
        mSwipeContainer.setEnabled(true);

        if(rss!=null){
            // clear all the old data
            mSimpleAdapter.clear();
            // add the new data
            mSimpleAdapter.addItems(rss);
            // display the list
            mRecyclerView.setVisibility(View.VISIBLE);

            if(mStartedLoading)//check if started loading not just rotation
                mRecyclerView.smoothScrollToPosition(0);// scroll back all the way to the top
        }else{
            // notify the user that we have connection problem
            mNoDataTextView.setVisibility(View.VISIBLE);
            // hide the data
            mRecyclerView.setVisibility(View.GONE);
            if (mTwoPane) {
                //hide the framelayout for the fragment
                mFrameLayout.setVisibility(View.GONE);
                //display the textView notice
                mPleaseSelectTextView.setVisibility(View.VISIBLE);

            }
        }
        mStartedLoading = false;
    }

    @Override
    public void onLoaderReset(Loader<RSS> loader) {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mLastSelectedNewsItem !=null)
            outState.putParcelable(CURRENT_TOPIC_KEY, mLastSelectedNewsItem);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState.containsKey(CURRENT_TOPIC_KEY)){
            mLastSelectedNewsItem = savedInstanceState.getParcelable(CURRENT_TOPIC_KEY);
            /*if is in dual pane state open the NewsItem in the other fragment*/
            if (mTwoPane) {
                openFragmentInDualPane(mLastSelectedNewsItem);
            }
        }
    }

    public void openFragmentInDualPane (NewsItem newsItem){
        //hide the textView notice
        mPleaseSelectTextView.setVisibility(View.GONE);
        //display the framelayout for the fragment
        mFrameLayout.setVisibility(View.VISIBLE);

        Bundle arguments = new Bundle();
        arguments.putParcelable(NewsItemDetailFragment.ARG_ITEM_ID, newsItem);
        NewsItemDetailFragment fragment = new NewsItemDetailFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.newsitem_detail_container, fragment)
                .commit();
    }

    public void openFragmentInActivity(NewsItem newsItem){
        // start the new activity
        Intent intent = new Intent(this, NewsItemDetailActivity.class);
        intent.putExtra(NewsItemDetailFragment.ARG_ITEM_ID, newsItem);
        startActivity(intent);
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
                    if(!mStartedLoading) {// not currently refreshing
                        mLastSelectedNewsItem = newsItem;
                        if (mTwoPane) {
                            openFragmentInDualPane(newsItem);
                        } else {
                            openFragmentInActivity(newsItem);
                        }
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
            public final TextView mTitleView;// news title
            public final TextView mPubDateView;// publish date
            public final ImageView mImageView; // Image associated with the news

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
