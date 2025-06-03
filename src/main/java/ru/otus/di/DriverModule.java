package ru.otus.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

public class DriverModule extends AbstractModule {

  @Override
  protected void configure() {

  }

  @Provides
  @Singleton
  public WebDriver provideWebDriver() throws MalformedURLException {
    boolean isMobile = Boolean.parseBoolean(System.getProperty("device", "false"));

    // ✅ Ստեղծում ենք selenoid:options map
    Map<String, Object> selenoidOptions = new HashMap<>();
    selenoidOptions.put("enableVNC", true);
    selenoidOptions.put("enableVideo", false);

    // ✅ Chrome mobile arguments
    List<String> chromeArgs = new ArrayList<>();
    if (isMobile) {
      chromeArgs.add("--window-size=375,812");
      chromeArgs.add("--user-agent=Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) "
          + "AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0 Mobile/15E148 Safari/604.1");
    }

    ChromeOptions options = new ChromeOptions();
    options.setBrowserVersion("116.0");
    options.setCapability("selenoid:options", selenoidOptions);
    if (!chromeArgs.isEmpty()) {
      options.addArguments(chromeArgs);
    }

    // ✅ Վերադարձնում ենք՝ pointing to Selenoid
    return new RemoteWebDriver(URI.create("http://localhost:4444/wd/hub").toURL(), options);

  }


}
