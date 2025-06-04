package ru.otus.page;

import com.google.inject.Inject;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.otus.util.WebDriverUtils;

public class MainPage {

  private final WebDriver driver;
  private final Actions actions;

  // Локатор на сам пункт меню «Обучение»:
  private static final By MENU_OBUCHENIE = By.xpath("//span[@title='Обучение']");

  // Локатор на выпадающие категории (ссылки):
  private static final By DROPDOWN_CATEGORIES = By.xpath("//a[contains(@href, '/categories/')]");

  @Inject
  public MainPage(WebDriver driver) {
    this.driver = driver;
    this.actions = new Actions(driver);
  }

  public void open() {
    driver.get("https://otus.ru/");
    acceptCookiesIfVisible();
  }

  /**
   * Если сайт показывает баннер с cookie, закроем его. (Подберите реальный локатор у себя, если он
   * есть.)
   */
  public void acceptCookiesIfVisible() {
    try {
      List<WebElement> cookies = new WebDriverWait(driver, Duration.ofSeconds(10)).until(
          ExpectedConditions.presenceOfAllElementsLocatedBy(
              By.cssSelector("button[data-testid='button-cookie']")));

      if (!cookies.isEmpty()) {
        WebDriverUtils.safeClick(cookies.get(0));
        System.out.println("✅ Cookie button clicked");
      }
    } catch (TimeoutException e) {
      System.out.println("⏳ Cookie button not found within timeout.");
    } catch (Exception e) {
      System.out.println("⚠️ Unexpected error in cookie click: " + e.getMessage());
    }
  }


  /**
   * Наводим курсор на «Обучение» (с ретраем для стабильности).
   */
  public void openDropdownWithRetry() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    int maxAttempts = 3;
    for (int attempt = 1; attempt <= maxAttempts; attempt++) {
      try {
        // Ждём, пока элемент будет кликабелен
        WebElement obuchenieLink = wait.until(
            ExpectedConditions.elementToBeClickable(MENU_OBUCHENIE));

        // Наводим курсор Actions'ами
        actions.moveToElement(obuchenieLink).perform();

        // Ждём, пока появятся ссылки в выпадающем меню
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(DROPDOWN_CATEGORIES));

        System.out.println("✅ Dropdown меню открыто (попытка " + attempt + ").");
        return;
      } catch (StaleElementReferenceException ex) {
        System.out.println("🔁 StaleElementReference, повторяем (попытка " + attempt + ")...");
      } catch (Exception e) {
        // исключения
        System.out.println("🔁 Иная ошибка: " + e.getMessage());
      }
    }
    throw new RuntimeException(
        "❌ Не удалось открыть меню «Обучение» за " + maxAttempts + " попыток.");
  }

  /**
   * Выбираем случайную категорию из открытого dropdown и кликаем по ней.
   */
  public String selectRandomCategory() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

    List<WebElement> links = wait.until(
        ExpectedConditions.visibilityOfAllElementsLocatedBy(DROPDOWN_CATEGORIES));

    if (links.isEmpty()) {
      throw new RuntimeException("❌ No category links found.");
    }

    links.forEach(el -> System.out.println("• Категория: " + el.getText()));

    WebElement randomLink = links.get(new Random().nextInt(links.size()));
    String categoryName = randomLink.getText();
    System.out.println("🎯 Выбрана категория: " + categoryName);

    // Прокручиваем к элементу и ждём, пока он станет кликабельным
    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});",
        randomLink);
    wait.until(ExpectedConditions.elementToBeClickable(randomLink));

    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", randomLink);

    return categoryName;
  }


  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }
}
