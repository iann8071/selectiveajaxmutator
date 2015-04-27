package jp.qr.java_conf.iann8071.ajaxmutator.proxy.plugin.client;

import com.google.common.collect.*;
import jp.qr.java_conf.iann8071.ajaxmutator.context.Context2;
import jp.qr.java_conf.iann8071.ajaxmutator.util.UrlUtil;
import org.owasp.webscarab.httpclient.HTTPClient;
import org.owasp.webscarab.model.Request;
import org.owasp.webscarab.model.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.rmi.runtime.Log;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collector;

/**
 * Created by iann8071 on 2015/04/13.
 */
public class AjaxMutatorHTTPClient implements HTTPClient {
    private HTTPClient mClient;
    private Table<String, String, BiFunction<Request, Response, Response>> mHandlers = ImmutableTable.of();
    private Logger logger = LoggerFactory.getLogger(getClass().getName());

    public static AjaxMutatorHTTPClient of(HTTPClient client, String url, Table<String, String, BiFunction<Request, Response, Response>> handlers){
        return new AjaxMutatorHTTPClient(client, url, handlers);
    }

    private AjaxMutatorHTTPClient(HTTPClient client) {
        mClient = client;
    }

    private AjaxMutatorHTTPClient(HTTPClient client, String url, Table<String, String, BiFunction<Request, Response, Response>> handlers) {
        mClient = client;
        mHandlers = handlers;
    }

    @Override
    public Response fetchResponse(Request request) throws IOException {
        request.deleteHeader("If-Modified-Since");
        request.deleteHeader("If-None-Match");
        Response response = mClient.fetchResponse(request);
        return UrlUtil.isSameDomain(Context2.url(), request.getURL().toString()) ?
                ImmutableMultimap.<String, String>builder().putAll(ContentHandler.handleContent(response.getHeader("Content-Type")), StatusCodeHandler.handleCode(response.getStatus())).build()
                        .entries().stream().map((Map.Entry<String, String> e) -> mHandlers.get(e.getKey(), e.getValue())).filter(e -> e != null).findAny().orElse((req, res) -> res).apply(request, response) :
                response;
    }
}
