package ru.otus.di;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import org.openqa.selenium.WebDriver;

public class DriverModule extends AbstractModule {
  @Override
  protected void configure() {
    // ✅ Միայն ՄԵԿ binding, առանց ScenarioScoped
    bind(WebDriver.class)
        .toProvider(DriverFactory.class)
        .in(Singleton.class); // ⚠️ Ոչ թե ScenarioScoped
  }
}
