package com.john_aziz57.rss_feed.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * Implements Parcelable to be passed from activity to another
 * Created by John on 11-Oct-16.
 */
@Root(strict = false)
public class MediaThumbnail implements Parcelable{
    /*mage width*/
    @Attribute
    public int width;
    /*imageheight*/
    @Attribute
    public int height;
    /*image url*/
    @Attribute
    public String url;
    /*Public constructor for SimpleXMLConversion*/
    public MediaThumbnail(){}

    protected MediaThumbnail(Parcel in) {
        width = in.readInt();
        height = in.readInt();
        url = in.readString();
    }

    public static final Parcelable.Creator<MediaThumbnail> CREATOR = new Parcelable.Creator<MediaThumbnail>() {
        @Override
        public MediaThumbnail createFromParcel(Parcel in) {
            return new MediaThumbnail(in);
        }

        @Override
        public MediaThumbnail[] newArray(int size) {
            return new MediaThumbnail[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(width);
        parcel.writeInt(height);
        parcel.writeString(url);
    }
}
