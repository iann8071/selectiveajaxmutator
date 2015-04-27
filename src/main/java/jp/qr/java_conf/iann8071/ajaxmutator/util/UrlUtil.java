package jp.qr.java_conf.iann8071.ajaxmutator.util;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * Created by iann8071 on 2015/04/14.
 */
public class UrlUtil {

    public static boolean isSameDomain(String url, String other) {
        try {
            return new URL(url).getHost().equals(new URL(other).getHost());
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public static String encode(String url) {
        try {
            return URLEncoder.encode(url, Charset.defaultCharset().toString());
        } catch(UnsupportedEncodingException e) {
            return null;
        }
    }
}
