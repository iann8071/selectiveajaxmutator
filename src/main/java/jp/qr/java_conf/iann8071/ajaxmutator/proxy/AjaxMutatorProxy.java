package jp.qr.java_conf.iann8071.ajaxmutator.proxy;

import com.google.common.collect.ImmutableTable;
import jp.qr.java_conf.iann8071.ajaxmutator.generator.mutant.Mutant;
import jp.qr.java_conf.iann8071.ajaxmutator.context.Context2;
import jp.qr.java_conf.iann8071.ajaxmutator.proxy.plugin.AjaxMutatorPlugin;
import jp.qr.java_conf.iann8071.ajaxmutator.util.Files2;
import jp.qr.java_conf.iann8071.ajaxmutator.util.Jsoup2;
import jp.qr.java_conf.iann8071.ajaxmutator.util.UrlUtil;
import org.jsoup.nodes.Element;
import org.owasp.webscarab.model.Preferences;
import org.owasp.webscarab.model.Request;
import org.owasp.webscarab.model.Response;
import org.owasp.webscarab.model.StoreException;
import org.owasp.webscarab.plugin.Framework;
import org.owasp.webscarab.plugin.proxy.Proxy;

import javax.naming.Context;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by iann8071 on 15/04/02.
 */
public class AjaxMutatorProxy {

    Proxy mProxy;

    private static AjaxMutatorProxy of(Supplier<AjaxMutatorPlugin> pluginCreator) {
        return new AjaxMutatorProxy(pluginCreator);
    }

    public static AjaxMutatorProxy forJsMutationOf(){
        return new AjaxMutatorProxy(
                () -> AjaxMutatorPlugin.of(Context2.url(),
                        () -> "JsMutatorPlugin",
                        () -> null));
    }

    public static AjaxMutatorProxy forRecordOf(){
        return new AjaxMutatorProxy(
                () -> AjaxMutatorPlugin.of(Context2.url(),
                        () -> "RecorderPlugin",
                        () -> ImmutableTable.<String, String, BiFunction<Request, Response, Response>>builder()
                                .put("HTML", "SUCCESS",
                                        (request, response) ->
                                        {
                                            List<Element> scripts = Jsoup2.parse(new ByteArrayInputStream(response.getContent()), request.getURL().toString())
                                                    .getElementsByTag("script").stream().filter(e -> e.attr("src").equals("")).collect(Collectors.toList());
                                            scripts.forEach(script ->
                                                    Files2.write(script.html(), Context2.jsNewOriginalFile(UrlUtil.encode(request.getURL().toString()))));
                                            return response;
                                        })
                                .put("JS", "SUCCESS",
                                        (request, response) ->
                                        {
                                            Files2.write(response.getContent(), Context2.jsNewOriginalFile(UrlUtil.encode(request.getURL().toString())));
                                            return response;
                                        })
                                .build()));
    }

    private AjaxMutatorProxy(Supplier<AjaxMutatorPlugin> pluginCreator) {
        try {
            Preferences.setPreference("AjaxMutatorProxy.listeners", "127.0.0.1:8080");
            Framework framework = new Framework();
            framework.setSession("FileSystem", new File(".conversation"), "");
            mProxy = new Proxy(framework);
            mProxy.addPlugin(pluginCreator != null ? pluginCreator.get() : AjaxMutatorPlugin.of());
        } catch (StoreException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        mProxy.run();
    }

    public void stop() {
        mProxy.stop();
        Files2.delete(new File(".conversation"));
    }
}
