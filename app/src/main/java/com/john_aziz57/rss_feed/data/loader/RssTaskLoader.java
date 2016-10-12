package com.john_aziz57.rss_feed.data.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.john_aziz57.rss_feed.data.model.RSS;
import com.john_aziz57.rss_feed.network.RetrofitManager;
import com.john_aziz57.rss_feed.network.RssService;

import java.io.IOException;

import retrofit2.Call;

/**
 * RssTaskLoader is responsible for fetching the data over the network through retrofit without
 * handling it in the main thread, evading the ANR message
 * Created by John on 11-Oct-16.
 */

public class RssTaskLoader extends AsyncTaskLoader<RSS> {
    public RssTaskLoader(Context context) {
        super(context);
    }

    @Override
    public RSS loadInBackground() {
        Log.v("RSS","Start Loading");
        RssService rssService = RetrofitManager.getInstance().create(RssService.class);
        Call<RSS> call = rssService.getRSS();
        RSS rss = null;
        try {
            rss = call.execute().body();
        }catch(IOException e){
            Log.e("RSS",e.getMessage());
        }
        finally{
            return rss;
        }
    }
}