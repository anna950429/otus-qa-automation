package ru.otus.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

public class DriverModule extends AbstractModule {

  @Override
  protected void configure() {
    // No bindings needed
  }

  @Provides
  public WebDriver provideWebDriver() throws MalformedURLException {
    boolean isMobile = Boolean.parseBoolean(System.getProperty("device", "false"));

    ChromeOptions options = new ChromeOptions();
    options.setCapability("browserVersion", "115.0");
    options.setCapability("pageLoadStrategy", "eager");

    Map<String, Object> selenoidOptions = new HashMap<>();
    selenoidOptions.put("enableVNC", true);
    selenoidOptions.put("enableVideo", false);
    options.setCapability("selenoid:options", selenoidOptions);

    options.addArguments("--disable-dev-shm-usage");
    options.addArguments("--no-sandbox");
    options.addArguments("--disable-gpu");
    options.addArguments("--disable-software-rasterizer");
    options.addArguments("--remote-allow-origins=*");
    options.addArguments("--disable-blink-features=AutomationControlled");

    if (isMobile) {
      options.addArguments("--window-size=375,812");
      options.addArguments("--user-agent=Mozilla/5.0 (iPhone; CPU iPhone OS 13_5 like Mac OS X)");
    } else {
      options.addArguments("--window-size=1920,1080");  // Use full HD screen
    }

    RemoteWebDriver driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), options);
    driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));

    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
    return driver;
  }
}
