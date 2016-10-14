package com.john_aziz57.rss_feed.data.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.LruCache;
import android.widget.Toast;

import com.john_aziz57.rss_feed.data.model.RSS;
import com.john_aziz57.rss_feed.network.RetrofitManager;
import com.john_aziz57.rss_feed.network.RssRetrofitService;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RssService extends Service {

    public static final String RESULT_DATA = "RESULT_DATA";
    private RSS mRss;
    private final IBinder mBinder = new LocalBinder();

    public RssService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public RssService getService() {
            return RssService.this;
        }
    }

    public void requestData(boolean forceLoad) {
        if (mRss == null || forceLoad) {
            Toast.makeText(this,"LOADING ...",Toast.LENGTH_SHORT).show();
            RssRetrofitService rssRetrofitService = RetrofitManager.getInstance().create(RssRetrofitService.class);
            Call<RSS> call = rssRetrofitService.getRSS();
            call.enqueue(new Callback<RSS>() {
                @Override
                public void onResponse(Call<RSS> call, Response<RSS> response) {
                    Intent resultIntent = new Intent(RESULT_DATA);
                    if (response.isSuccessful()) {
                        mRss = response.body();
                    }else{
                        mRss = null;
                    }
                    resultIntent.putExtra(RESULT_DATA, mRss);
                    LocalBroadcastManager.getInstance(RssService.this).sendBroadcast(resultIntent);
                }

                @Override
                public void onFailure(Call<RSS> call, Throwable t) {
                    Intent resultIntent = new Intent(RESULT_DATA);
                    mRss  = null;
                    resultIntent.putExtra(RESULT_DATA, mRss);
                    LocalBroadcastManager.getInstance(RssService.this).sendBroadcast(resultIntent);
                }
            });
        }
        else// not force load
        {
            Intent resultIntent = new Intent(RESULT_DATA);
            resultIntent.putExtra(RESULT_DATA, mRss);
            LocalBroadcastManager.getInstance(RssService.this).sendBroadcast(resultIntent);
        }

    }

}
