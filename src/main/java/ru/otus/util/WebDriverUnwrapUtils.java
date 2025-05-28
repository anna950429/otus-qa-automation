package ru.otus.util;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WrapsDriver;

public class WebDriverUnwrapUtils {
  public static WebDriver unwrap(WebDriver driver) {
    if (driver instanceof WrapsDriver) {
      return ((WrapsDriver) driver).getWrappedDriver();
    }
    return driver;
  }
}
