package ru.otus.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import org.openqa.selenium.WebDriver;

public class DriverModule extends AbstractModule {
  @Override
  protected void configure() {
    try {
      // Попробовать загрузить ScenarioScoped класс
      Class<?> scope = Class.forName("io.cucumber.guice.ScenarioScoped");

      // Попробовать привязать WebDriver с этим scope (работает только при запуске из Cucumber)
      bind(WebDriver.class)
          .toProvider((Class<? extends Provider<WebDriver>>) Class.forName("ru.otus.di.DriverFactory"))
          .in((Class) scope);
    } catch (ClassNotFoundException | NoClassDefFoundError e) {
      // Если ScenarioScoped не доступен (например, обычный JUnit test) — fallback
      bind(WebDriver.class)
          .toProvider(ru.otus.di.DriverFactory.class)
          .in(Scopes.SINGLETON);
    }
  }
}
