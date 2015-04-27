package jp.qr.java_conf.iann8071.ajaxmutator.test;

/**
 * Created by iann8071 on 15/04/02.
 */

import jp.qr.java_conf.iann8071.ajaxmutator.proxy.AjaxMutatorProxy;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class AjaxMutationTest {
    private AjaxMutatorProxy proxy;
    private Class<?> test;
    public static String DEFAULT_HOST = "127.0.0.1";
    public static int DEFAULT_PORT = 80;

    public Result run() {
        return JUnitCore.runClasses(test);
    }

    public AjaxMutationTest setTest(Class<?> test){
        this.test = test;
        return this;
    }
/*
    public AjaxMutationTest setProxy() {
        proxy = newProxy(DEFAULT_HOST, DEFAULT_PORT);
        return this;
    }

    public AjaxMutationTest setProxy(String host, int port) {
        proxy = newProxy(host, port);
        return this;
    }
*/
    public AjaxMutationTest measureMutationScore(){
        return this;
    }

    public AjaxMutationTest measureCoverageScore(){
        return this;
    }

    public AjaxMutationTest measureMutationHigerOrderMutationScore(){
        return this;
    }

    public AjaxMutationTest measureCategorySelectiveMutationScore(){
        return this;
    }

    public AjaxMutationTest measureNSelectiveMutationScore(){
        return this;
    }

    public AjaxMutationTest measureStatementSelectiveMutationScore(){
        return this;
    }
}
