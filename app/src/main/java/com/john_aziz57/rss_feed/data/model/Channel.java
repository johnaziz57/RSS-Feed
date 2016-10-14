package com.john_aziz57.rss_feed.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by John on 11-Oct-16.
 */
@Root(strict = false)
public class Channel implements Parcelable {
    /*news items in an inline list*/
    @ElementList(entry = "item" , inline = true)
    public List<NewsItem> list;
    @Element
    public String title;
    @Element
    public String description;
    @Element
    public String link;
    @Element
    public String lastBuildDate;
    @Element
    public String language;

    public Channel(){}

    protected Channel(Parcel in) {
        list = in.createTypedArrayList(NewsItem.CREATOR);
        title = in.readString();
        description = in.readString();
        link = in.readString();
        lastBuildDate = in.readString();
        language = in.readString();
    }

    public static final Creator<Channel> CREATOR = new Creator<Channel>() {
        @Override
        public Channel createFromParcel(Parcel in) {
            return new Channel(in);
        }

        @Override
        public Channel[] newArray(int size) {
            return new Channel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(list);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(link);
        parcel.writeString(lastBuildDate);
        parcel.writeString(language);
    }
}
