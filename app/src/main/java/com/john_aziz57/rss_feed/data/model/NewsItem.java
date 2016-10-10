package com.john_aziz57.rss_feed.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

/**
 * Created by John on 11-Oct-16.
 */
@Root
public class NewsItem implements Parcelable{
    @Element
    public String title;
    @Element
    public String description;
    @Element
    public String link;
    @Element
    public String guid;
    @Element
    public String pubDate;

    /*the object the hold the image*/
    @Element
    @Namespace
    public MediaThumbnail thumbnail;

    protected NewsItem(Parcel in) {
        title = in.readString();
        description = in.readString();
        link = in.readString();
        guid = in.readString();
        pubDate = in.readString();
        thumbnail = in.readParcelable(MediaThumbnail.class.getClassLoader());
    }

    public static final Creator<NewsItem> CREATOR = new Creator<NewsItem>() {
        @Override
        public NewsItem createFromParcel(Parcel in) {
            return new NewsItem(in);
        }

        @Override
        public NewsItem[] newArray(int size) {
            return new NewsItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(link);
        parcel.writeString(guid);
        parcel.writeString(pubDate);
        parcel.writeParcelable(thumbnail,0);
    }
}
