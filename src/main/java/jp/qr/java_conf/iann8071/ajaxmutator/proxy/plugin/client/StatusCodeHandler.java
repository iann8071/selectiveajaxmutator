package jp.qr.java_conf.iann8071.ajaxmutator.proxy.plugin.client;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by iann8071 on 2015/04/13.
 */
public class StatusCodeHandler {

    public static final String OK = "200";
    public static final String CREATED = "201";
    public static final String ACCEPTED = "202";
    public static final String NO_CONTENT = "204";
    public static final String SUCCESS = "SUCCESS";
    public static final String MOVED_PERMANENTLY = "301";
    public static final String MOVED_TEMPORARILY = "302";
    public static final String NOT_MODIFIED = "304";
    public static final String REDIRECTION = "REDIRECTION";
    public static final String BAD_REQUEST = "400";
    public static final String UNAUTHORIZED = "401";
    public static final String FORBIDDEN = "403";
    public static final String NOT_FOUND = "404";
    public static final String CLIENT_ERROR = "CLIENT_ERROR";
    public static final String INTERNAL_SERVER_ERROR = "500";
    public static final String NOT_IMPLEMENTED = "501";
    public static final String BAD_GATEWAY = "502";
    public static final String SERVICE_ANAVAILABLE = "503";
    public static final String SERVER_ERROR = "SERVER_ERROR";

    private List<Function<String, String>> mHandlers = ImmutableList.of(
            code -> Integer.valueOf(code) == 200 ? OK : null,
            code -> Integer.valueOf(code) == 201 ? CREATED : null,
            code -> Integer.valueOf(code) == 202 ? ACCEPTED : null,
            code -> Integer.valueOf(code) == 204 ? NO_CONTENT : null,
            code -> 200 <= Integer.valueOf(code) && Integer.valueOf(code) < 300 ? SUCCESS : null,
            code -> Integer.valueOf(code) == 301 ? MOVED_PERMANENTLY : null,
            code -> Integer.valueOf(code) == 302 ? MOVED_TEMPORARILY : null,
            code -> Integer.valueOf(code) == 304 ? NOT_MODIFIED : null,
            code -> 300 <= Integer.valueOf(code) && Integer.valueOf(code) < 400 ? REDIRECTION : null,
            code -> Integer.valueOf(code) == 400 ? BAD_REQUEST : null,
            code -> Integer.valueOf(code) == 401 ? UNAUTHORIZED : null,
            code -> Integer.valueOf(code) == 403 ? FORBIDDEN : null,
            code -> Integer.valueOf(code) == 404 ? NOT_FOUND : null,
            code -> 400 <= Integer.valueOf(code) && Integer.valueOf(code) < 500 ? CLIENT_ERROR : null,
            code -> Integer.valueOf(code) == 500 ? INTERNAL_SERVER_ERROR : null,
            code -> Integer.valueOf(code) == 501 ? NOT_IMPLEMENTED : null,
            code -> Integer.valueOf(code) == 502 ? BAD_GATEWAY : null,
            code -> Integer.valueOf(code) == 503 ? SERVICE_ANAVAILABLE : null,
            code -> 500 <= Integer.valueOf(code) && Integer.valueOf(code) < 600 ? SERVER_ERROR : null
    );

    public static List<String> handleCode(String code){
        return new StatusCodeHandler().mHandlers.stream().map(handler -> handler.apply(code)).filter(c -> c != null).collect(Collectors.toList());
    }
}
