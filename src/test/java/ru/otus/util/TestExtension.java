package ru.otus.util;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import io.cucumber.guice.ScenarioScoped;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.WebDriver;
import ru.otus.di.DriverModule;

public class TestExtension implements BeforeAllCallback, BeforeEachCallback, AfterAllCallback {

  private static Injector injector;

  @Override
  public void beforeAll(ExtensionContext context) {
    injector = Guice.createInjector(

        // твой модуль с биндингами драйвера
        new DriverModule(),

        // 🔑 ДОБАВЛЯЕМ fallback‑модуль, который
        //     «говорит» Guice, что @ScenarioScoped = Singleton
        new AbstractModule() {
          @Override
          protected void configure() {
            bindScope(ScenarioScoped.class, Scopes.SINGLETON);
          }
        });
  }

  @Override
  public void beforeEach(ExtensionContext context) {
    injector.injectMembers(context.getRequiredTestInstance());
  }

  @Override
  public void afterAll(ExtensionContext context) {
    injector.getInstance(WebDriver.class).quit();
  }
}
