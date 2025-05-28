package ru.otus.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.events.EventFiringDecorator;
import ru.otus.util.WebDriverHighlightListener;

public class DriverModule extends AbstractModule {

  @Override
  protected void configure() {
    // пустой configure, если нет биндингов
  }

  @Provides
  @Singleton
  public WebDriver provideWebDriver() {

    // 1) Автоматическая подстановка нужной версии chromedriver
    // WebDriverManager.chromedriver().setup();
    System.setProperty("webdriver.chrome.driver",
        "/Users/an.petrosyan/Downloads/chromedriver-mac-arm64/chromedriver");

    ChromeOptions options = new ChromeOptions();

    // 2) Отключаем сообщение "Chrome is being controlled by automated test software"
    options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));

    // 3) Отключаем Automation Extension
    options.setExperimentalOption("useAutomationExtension", false);

    // 4) Маскируем автоматизацию
    options.addArguments("--disable-blink-features=AutomationControlled");

    // 5) Разрешаем любой origin (особенно актуально для новых версий Chrome)
    options.addArguments("--remote-allow-origins=*");

    // 6) Создаём временную папку-профиль, чтобы не мешать основному профилю
    String profileDir =
        System.getProperty("java.io.tmpdir") + "/chrome-profile-" + UUID.randomUUID();
    options.addArguments("--user-data-dir=" + profileDir);

    // 7) Запуск без GUI (headless), если нужно. (Закомментируйте, если хотите браузер с UI)
    // options.addArguments("--headless=new");

    // 8) Отключаем нотификации
    options.addArguments("--disable-notifications");

    // 9) Игнорируем предупреждения об SSL
    options.addArguments("--ignore-certificate-errors");

    // 10) Можно добавить другие оптимизации
    // (например, "--disable-gpu", "--no-sandbox" — в Docker или CI)

    ChromeDriver baseDriver = new ChromeDriver(options);

    // Оборачиваем в EventFiringDecorator, чтобы заработал ваш WebDriverHighlightListener
    WebDriver driver = new EventFiringDecorator(new WebDriverHighlightListener()).decorate(
        baseDriver);

    // Максимализируем окно
    driver.manage().window().maximize();

    // Ставим неявное ожидание
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

    return driver;
  }
}
