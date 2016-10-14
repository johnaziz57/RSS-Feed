package com.john_aziz57.rss_feed.ui.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
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
import com.john_aziz57.rss_feed.data.model.NewsItem;
import com.john_aziz57.rss_feed.data.model.RSS;
import com.john_aziz57.rss_feed.data.service.RssService;
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
public class NewsItemListActivity extends AppCompatActivity {

    /*Current NewsItem key for restoring state*/
    private static final String CURRENT_TOPIC_KEY = "CURRENT_TOPIC";

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mDualPane;
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

    private ResponseReceiver mResponseReceiver;

    private RssService mBoundService;
    private boolean mIsBound;

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

        /*check if using dual pane view*/
        if (findViewById(R.id.newsitem_detail_container) != null) {
            mDualPane = true;
            mFrameLayout = (FrameLayout) findViewById(R.id.newsitem_detail_container);
            mPleaseSelectTextView = (TextView) findViewById(R.id.please_select_textview);
        }

        mNoDataTextView = (TextView) findViewById(R.id.no_data_textview);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        //setup swipe to refresh container
        mSwipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!mStartedLoading)// not already refreshing
                    mBoundService.requestData(true);
                mStartedLoading = true;
            }
        });

        mSwipeContainer.setColorSchemeResources(R.color.colorPrimary,R.color.colorPrimaryDark,R.color.colorAccent);
        // disable swipe to refresh because we are already loading the data, don't refresh
        mSwipeContainer.setEnabled(false);

        Intent serviceIntent = new Intent(this,RssService.class);
        startService(serviceIntent);
        doBindService();
        IntentFilter mStatusIntentFilter = new IntentFilter(RssService.RESULT_DATA);
        mResponseReceiver = new ResponseReceiver();
        // Registers the DownloadStateReceiver and its intent filters
        LocalBroadcastManager.getInstance(this).registerReceiver(mResponseReceiver,mStatusIntentFilter);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        mSimpleAdapter = new SimpleItemRecyclerViewAdapter(new LinkedList<NewsItem>());
        recyclerView.setAdapter(mSimpleAdapter);
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
            if (mDualPane) {
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
                        if (mDualPane) {
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
            final View mView;
            final TextView mTitleView;// news title
            final TextView mPubDateView;// publish date
            final ImageView mImageView; // Image associated with the news

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

    //////////////////////// service
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mBoundService = ((RssService.LocalBinder)service).getService();
            mBoundService.requestData(false);
            mStartedLoading = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            mBoundService = null;
        }
    };

    void doBindService() {
        bindService(new Intent(this,RssService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mResponseReceiver);
    }

    private class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            RSS rss = intent.getParcelableExtra(RssService.RESULT_DATA);
            onLoadFinish(rss);
        }
    }

    /**
     * Change the view according to the return result from the service
     * if rss parameter is null will display no connection
     * else will display proper data
     * @param rss
     */
    void onLoadFinish(RSS rss){
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
            if (mDualPane) {
                //hide the framelayout for the fragment
                mFrameLayout.setVisibility(View.GONE);
                //display the textView notice
                mPleaseSelectTextView.setVisibility(View.VISIBLE);

            }
        }
        mStartedLoading = false;
    }

}
