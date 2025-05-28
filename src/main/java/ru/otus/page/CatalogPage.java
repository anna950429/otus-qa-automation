package ru.otus.page;

import com.google.inject.Inject;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.otus.util.DateUtils;
import ru.otus.util.WaitUtils;

/**
 * Page Object для страницы каталога курсов OTUS (https://otus.ru/catalog/courses).
 */
public class CatalogPage {

  private final WebDriver driver;
  private static final By SHOW_MORE_BUTTON = By.xpath("//button[contains(text(), 'Показать еще')]");

  @Inject
  public CatalogPage(WebDriver driver) {
    this.driver = driver;
  }

  public WebDriver getDriver() {
    return driver;
  }

  /**
   * Открывает страницу каталога курсов.
   */
  public void open() {
    driver.get("https://otus.ru/catalog/courses");
  }

  /**
   * Открывает страницу каталога с переданной строкой запроса (поиск).
   * Пример: "?search=QA"
   */
  public void openWithSearch(String query) {
    String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);
    driver.get("https://otus.ru/catalog/courses?search=" + encoded);

    // Ждём, пока страница действительно отобразит хотя бы что-то, содержащее "QA"
    WaitUtils.getWait(driver, 20).until(
        d -> d.getPageSource().contains(query)
    );
  }

  /**
   * Нажимает на кнопку "Показать еще" до тех пор, пока количество курсов продолжает расти.
   * Либо пока не будет подряд несколько "холостых" кликов.
   */
  public void clickShowMoreUntilEnd() {
    int lastCount = 0;
    int stableTries = 0;

    while (stableTries < 3) {
      try {
        // Прокрутить страницу вниз
        ((JavascriptExecutor) driver).executeScript(
            "window.scrollTo(0, document.body.scrollHeight);");
        Thread.sleep(500); // небольшая пауза для подгрузки

        // Ищем кнопку "Показать ещё"
        List<WebElement> buttons = driver.findElements(SHOW_MORE_BUTTON);
        if (!buttons.isEmpty()) {
          WebElement button = buttons.get(0);
          if (button.isDisplayed() && button.isEnabled()) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
            Thread.sleep(700); // пауза для стабильной подгрузки
          }
        }

        // Проверяем, изменилось ли количество курсов
        List<WebElement> courses = driver.findElements(By.cssSelector("a[href^='/lessons/']"));
        int currentCount = courses.size();
        if (currentCount > lastCount) {
          lastCount = currentCount;
          stableTries = 0; // сбросить счётчик, раз курсы действительно подгрузились
        } else {
          stableTries++;
        }

      } catch (Exception e) {
        System.out.println("⚠️ Ошибка при прокрутке/клике: " + e.getMessage());
        break;
      }
    }
    System.out.println("Итоговое количество загруженных курсов: " + lastCount);
  }

  /**
   * Собирает информацию о курсах, отображающихся на странице, с помощью jsoup.
   * Здесь обязательно должны быть корректные селекторы под реальную верстку.
   *
   * @return список курсов (title, href, startDate, price)
   */
  public List<CourseData> getAllCoursesFromJsoup() {
    String html = driver.getPageSource();
    Document doc = Jsoup.parse(html);

    // Пример: ищем элементы с классом "course-card"
    // ПОДСТАВЬТЕ РЕАЛЬНЫЙ CSS-СЕЛЕКТОР ИЗ ВАШЕЙ ВЕРСТКИ
    Elements courseElements = doc.select("a.sc-zzdkm7-0");
    List<CourseData> result = new ArrayList<>();

    for (Element el : courseElements) {
      String href = el.attr("href").trim(); // /lessons/...

      String title = el.select("h6").text();

      String dateText = el.select("div.sc-hrqzy3-1.jEGzDf").text();
      LocalDate startDate = DateUtils.parseDateFromOtusText(dateText);

      String priceText = el.select("div.sc-hrqzy3-1.jEGzDf").text();
      int price = parsePrice(priceText);

      result.add(new CourseData(title, href, startDate,price));
    }
    return result;
  }

  /**
   * Удаляем все не-цифры из строки цены и конвертируем в int.
   * Если не удалось — вернётся 0.
   */
  private int parsePrice(String priceText) {
    // Удаляем всё, что не цифра
    String digits = priceText.replaceAll("\\D+", "");
    if (digits.isEmpty()) {
      return 0;
    }
    return Integer.parseInt(digits);
  }

  /**
   * Ожидаем, что на странице появится текст courseName
   */
  public void waitForCourseToAppear(String courseName) {
    new WebDriverWait(driver, Duration.ofSeconds(10)).until(
        d -> d.getPageSource().toLowerCase().contains(courseName.toLowerCase()));
  }

  /**
   * Выводим в консоль названия всех курсов (debug).
   */
  public void debugPrintAllCourseTitles() {
    System.out.println("Список курсов (debug):");
    getAllCoursesFromJsoup().forEach(course -> System.out.println(" - " + course.title()));
  }

  /**
   * Находит курс по названию (через строчное вхождение) и возвращает Optional<CourseData>.
   */
  public Optional<CourseData> findCourseByTitle(String targetTitle) {
    return getAllCoursesFromJsoup().stream()
        .filter(c -> c.title().toLowerCase().contains(targetTitle.toLowerCase()))
        .findFirst();
  }

}
