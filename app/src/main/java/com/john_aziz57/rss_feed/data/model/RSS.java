package com.john_aziz57.rss_feed.data.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by John on 11-Oct-16.
 */
@Root
public class RSS {
    @Element
    public Channel channel;
}
