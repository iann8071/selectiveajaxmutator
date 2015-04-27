package webdriver;

import jp.qr.java_conf.iann8071.ajaxmutator.context.Context2;
import jp.qr.java_conf.iann8071.ajaxmutator.proxy.AjaxMutatorProxy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.concurrent.TimeUnit;

/**
 * Created by iann8071 on 2015/04/12.
 */
public class RecorderProxyTest {

    WebDriver driver;
    AjaxMutatorProxy proxy;

    @Before
    public void setup(){
        Context2.url("http://localhost/");
        proxy = AjaxMutatorProxy.forRecordOf();
        proxy.start();
        Proxy proxy = new Proxy().setHttpProxy("127.0.0.1:8008");
        DesiredCapabilities cap = new DesiredCapabilities();
        cap.setCapability(CapabilityType.PROXY, proxy);
        driver = new FirefoxDriver(cap);
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    }

    //@Test
    public void test() {
        driver.get("http://localhost/wordpress");
    }

    @After
    public void quit(){
        proxy.stop();
        driver.quit();
    }
}
