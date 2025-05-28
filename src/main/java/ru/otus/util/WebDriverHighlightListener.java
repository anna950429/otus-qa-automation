package ru.otus.util;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsDriver;
import org.openqa.selenium.support.events.WebDriverListener;

public class WebDriverHighlightListener implements WebDriverListener {

  private static final String ORIGINAL_STYLE_KEY = "original_style";
  private static final String HIGHLIGHT_STYLE = "border: 2px solid red; background: yellow;";

  @Override
  public void beforeClick(WebElement element) {
    highlightElement(element);
  }

  @Override
  public void afterClick(WebElement element) {
    restoreElementStyle(element);
  }

  @Override
  public void beforeSendKeys(WebElement element, CharSequence... keysToSend) {
    highlightElement(element);
  }

  @Override
  public void afterSendKeys(WebElement element, CharSequence... keysToSend) {
    restoreElementStyle(element);
  }


  private void highlightElement(WebElement element) {
    JavascriptExecutor js = (JavascriptExecutor) ((WrapsDriver) element).getWrappedDriver();
    // Сохраняем текущий стиль в некий атрибут:
    js.executeScript("var elem = arguments[0];" + "var currentStyle = elem.getAttribute('style');"
        + "elem.setAttribute(arguments[1], currentStyle);", element, ORIGINAL_STYLE_KEY);

    // Устанавливаем подсветку
    js.executeScript("arguments[0].setAttribute('style', arguments[1]);", element, HIGHLIGHT_STYLE);
  }

  private void restoreElementStyle(WebElement element) {
    try {
      JavascriptExecutor js = (JavascriptExecutor) ((WrapsDriver) element).getWrappedDriver();
      js.executeScript(
          "var elem = arguments[0];" + "var originalStyle = elem.getAttribute(arguments[1]);"
              + "if (originalStyle) {" + "  elem.setAttribute('style', originalStyle);" + "} else {"
              + "  elem.removeAttribute('style');" + "}", element, ORIGINAL_STYLE_KEY);
    } catch (org.openqa.selenium.StaleElementReferenceException e) {
      System.out.println("⚠️ Element became stale before restoring style. Ignoring.");
    }
  }


}
