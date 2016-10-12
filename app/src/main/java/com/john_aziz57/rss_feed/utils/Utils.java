package com.john_aziz57.rss_feed.utils;

import android.text.Html;
import android.text.Spanned;

/**
 * Created by John on 12-Oct-16.
 */

public class Utils {
    private static final String LINK_FIRST_HALF = "<a href=\"";
    private static final String LINK_SECOND_HALF = "\">More...</a></string>";
    public static Spanned getTheMoreLink(String url){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(LINK_FIRST_HALF+url+LINK_SECOND_HALF,Html.FROM_HTML_MODE_LEGACY);
        } else {
            return  Html.fromHtml(LINK_FIRST_HALF+url+LINK_SECOND_HALF);
        }

    }
}
