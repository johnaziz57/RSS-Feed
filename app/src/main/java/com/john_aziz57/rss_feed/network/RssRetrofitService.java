package com.john_aziz57.rss_feed.network;

import com.john_aziz57.rss_feed.data.model.RSS;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by John on 11-Oct-16.
 */

public interface RssRetrofitService {
    String RSS_URL = "http://feeds.bbci.co.uk/news/";

    @GET("rss.xml")
    Call<RSS> getRSS();
}
