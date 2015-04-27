package jp.qr.java_conf.iann8071.ajaxmutator.proxy.plugin.client;

/**
 * Created by iann8071 on 2015/04/13.
 */
public class ContentHandler {

    public static final String JS = "JS";
    public static final String HTML = "HTML";
    public static final String OTHER = "OTHER";

    public static String handleContent(String contentType){
        return  contentType.contains("text/javascript") ||
                contentType.contains("application/javascript") || contentType.contains("text/ecmascript") ||
                contentType.contains("application/ecmascript") || contentType.contains("application/x-javascript") ? JS :
                contentType.contains("text/html") ? HTML : OTHER;
    }
}
