package ru.otus.util;

import org.openqa.selenium.WebElement;

public class WebDriverUtils {

  public static void safeClick(WebElement element) {
    for (int i = 0; i < 3; i++) {
      try {
        element.click();
        return;
      } catch (Exception e) {
        System.out.println("⚠️ Retry click " + (i + 1));
        try {
          Thread.sleep(1000);
        } catch (InterruptedException ignored) {
          Thread.currentThread().interrupt(); // restore the interrupted flag
          System.err.println("⛔ Thread interrupted during retry delay: " + e.getMessage());
        }
      }
    }
    throw new RuntimeException("❌ Failed to click after 3 retries");
  }
}
