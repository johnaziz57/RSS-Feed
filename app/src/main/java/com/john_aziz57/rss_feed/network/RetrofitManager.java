package com.john_aziz57.rss_feed.network;

/**
 * Created by John on 11-Oct-16.
 */

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * RetrofitManager is used to get instance of retrofit for network calls, retrofit is singleton
 */
public class RetrofitManager {
    private static Retrofit retrofit;

    public static Retrofit getInstance(){
        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    /*RSS url*/
                    .baseUrl(RssRetrofitService.RSS_URL)
                    .client(new OkHttpClient())
                    /*Adding XML converter*/
                    .addConverterFactory(SimpleXmlConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
