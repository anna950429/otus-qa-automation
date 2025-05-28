package ru.otus.util;

import java.time.Duration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WaitUtils {

  private WaitUtils() {
  }

  public static WebElement waitForClickable(WebDriver driver, WebElement element, long seconds) {
    return new WebDriverWait(driver, Duration.ofSeconds(seconds)).until(
        ExpectedConditions.elementToBeClickable(element));
  }

  public static WebElement waitForVisible(WebDriver driver, WebElement element, long seconds) {
    return new WebDriverWait(driver, Duration.ofSeconds(seconds)).until(
        ExpectedConditions.visibilityOf(element));
  }

  public static WebDriverWait getWait(WebDriver driver, long seconds) {
    return new WebDriverWait(driver, Duration.ofSeconds(seconds));
  }
}