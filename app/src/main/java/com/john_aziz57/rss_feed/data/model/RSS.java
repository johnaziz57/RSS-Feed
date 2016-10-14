package com.john_aziz57.rss_feed.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by John on 11-Oct-16.
 */
@Root(strict=false)
public class RSS implements Parcelable{
    @Element
    public Channel channel;

    public RSS(){}

    protected RSS(Parcel in) {
        channel = in.readParcelable(Channel.class.getClassLoader());
    }

    public static final Creator<RSS> CREATOR = new Creator<RSS>() {
        @Override
        public RSS createFromParcel(Parcel in) {
            return new RSS(in);
        }

        @Override
        public RSS[] newArray(int size) {
            return new RSS[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(channel,0);
    }
}