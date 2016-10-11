package com.john_aziz57.rss_feed.data.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by John on 11-Oct-16.
 */
@Root(strict = false)
public class Channel {
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
}
