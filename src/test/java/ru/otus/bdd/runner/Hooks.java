package ru.otus.bdd.runner;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.openqa.selenium.WebDriver;
import ru.otus.di.DriverModule;

/**
 * Хуки для Cucumber, чтобы инициализировать Guice + WebDriver
 */
public class Hooks {

  private static Injector injector;
  private static WebDriver driver;

  @Before
  public void setUp() {
    // Создаем Injector
    injector = Guice.createInjector(new DriverModule());
    // Получаем WebDriver
    driver = injector.getInstance(WebDriver.class);
  }

  @After
  public void tearDown() {
    if (driver != null) {
      driver.quit();
    }
  }

  // Геттер
  public static WebDriver getDriver() {
    return driver;
  }

  public static Injector getInjector() {
    return injector;
  }
}
