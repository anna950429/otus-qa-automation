package ru.otus.di;

import com.google.inject.Provider;
import com.google.inject.Singleton;
import java.io.Closeable;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.events.EventFiringDecorator;
import ru.otus.util.WebDriverHighlightListener;

@Singleton
public class DriverFactory implements Provider<WebDriver>, Closeable {

  private final WebDriver driver;

  public DriverFactory() {
    //WebDriverManager.chromedriver().setup();
    System.setProperty("webdriver.chrome.driver",
        "/Users/an.petrosyan/Downloads/chromedriver-mac-arm64 2/chromedriver");

    ChromeOptions options = new ChromeOptions();
    options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
    options.setExperimentalOption("useAutomationExtension", false);
    options.addArguments("--disable-blink-features=AutomationControlled");
    options.addArguments("--remote-allow-origins=*");
    options.addArguments(
        "--user-data-dir=" + System.getProperty("java.io.tmpdir") + "/chrome-profile-"
            + UUID.randomUUID());
    options.addArguments("--disable-notifications");
    options.addArguments("--ignore-certificate-errors");

    if (Boolean.parseBoolean(System.getProperty("headless", "false"))) {
      options.addArguments("--headless=new");
      options.addArguments("--disable-gpu");
      options.addArguments("--no-sandbox");
    }
    ChromeDriver base = new ChromeDriver(options);
    driver = new EventFiringDecorator(new WebDriverHighlightListener()).decorate(base);
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    driver.manage().window().maximize();
  }

  @Override
  public WebDriver get() {
    return driver;
  }

  @Override
  public void close() throws IOException {
    driver.quit();
  }
}
