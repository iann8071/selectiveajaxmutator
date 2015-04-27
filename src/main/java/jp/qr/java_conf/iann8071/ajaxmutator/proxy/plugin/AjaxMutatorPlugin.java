package jp.qr.java_conf.iann8071.ajaxmutator.proxy.plugin;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import jp.qr.java_conf.iann8071.ajaxmutator.proxy.plugin.client.AjaxMutatorHTTPClient;
import org.owasp.webscarab.httpclient.HTTPClient;
import org.owasp.webscarab.model.Request;
import org.owasp.webscarab.model.Response;
import org.owasp.webscarab.plugin.proxy.ProxyPlugin;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Created by iann8071 on 2015/04/14.
 */
public class AjaxMutatorPlugin extends ProxyPlugin {
    private String mUrl;
    private Supplier<String> mPluginNameGetter;
    private Supplier<Table<String, String, BiFunction<Request, Response, Response>>> mHandlerTableGetter;

    public static AjaxMutatorPlugin of(){
        return new AjaxMutatorPlugin(null, null, null);
    }

    public static AjaxMutatorPlugin of(String url, Supplier<String> pluginNameGetter,
                                       Supplier<Table<String, String, BiFunction<Request, Response, Response>>> handlerTableGetter){
        return new AjaxMutatorPlugin(url, pluginNameGetter, handlerTableGetter);
    }

    private AjaxMutatorPlugin(String url, Supplier<String> pluginNameGetter,
                              Supplier<Table<String, String, BiFunction<Request, Response, Response>>> handlerTableGetter) {
        mUrl = url;
        mPluginNameGetter = pluginNameGetter;
        mHandlerTableGetter = handlerTableGetter;
    }

    @Override
    public String getPluginName() {
        return mPluginNameGetter != null ? mPluginNameGetter.get() : "AjaxMutatorPlugin";
    }

    @Override
    public HTTPClient getProxyPlugin(HTTPClient client) {
        return AjaxMutatorHTTPClient.of(client, Optional.of(mUrl).orElse("http://localhost/"),  mHandlerTableGetter != null ? mHandlerTableGetter.get() : ImmutableTable.of());
    }
}
