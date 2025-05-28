package ru.otus.util;

import com.google.inject.Guice;
import com.google.inject.Injector;
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
    injector = Guice.createInjector(new DriverModule());
  }

  @Override
  public void beforeEach(ExtensionContext context) {
    Object testInstance = context.getRequiredTestInstance();
    injector.injectMembers(testInstance);
  }

  @Override
  public void afterAll(ExtensionContext context) {
    WebDriver driver = injector.getInstance(WebDriver.class);
    driver.quit();
  }
}
