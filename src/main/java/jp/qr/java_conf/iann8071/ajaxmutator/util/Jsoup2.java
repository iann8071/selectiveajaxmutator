package jp.qr.java_conf.iann8071.ajaxmutator.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Created by iann8071 on 2015/04/13.
 */
public class Jsoup2 {
    public static Document parse(InputStream in, String baseUri){
        try {
            return Jsoup.parse(in, Charset.defaultCharset().toString(), baseUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
