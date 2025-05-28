package ru.otus.page;

import com.google.inject.Inject;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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

public class CatalogPage {

  private final WebDriver driver;
  private static final By SHOW_MORE_BUTTON = By.xpath("//button[contains(text(), 'Показать еще')]");

  @Inject
  public CatalogPage(WebDriver driver) {
    this.driver = driver;
  }

  public WebDriver getDriver() {
    return this.driver;
  }

  public void open() {
    driver.get("https://otus.ru/catalog/courses");
  }

  public void openWithSearch(String query) {
    String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);
    driver.get("https://otus.ru/catalog/courses?search=" + encoded);

    WaitUtils.getWait(driver, 20).until(d -> d.getPageSource().contains("QA"));
  }

  public List<CourseData> getAllCoursesFromJsoup() {
    String html = driver.getPageSource();
    Document doc = Jsoup.parse(html);

    Elements courseLinks = doc.select("a[href^='/lessons/']");

    List<CourseData> courses = new ArrayList<>();

    for (Element link : courseLinks) {
      String href = link.attr("href").trim();
      String fullText = link.text().trim();

      if (fullText.isEmpty() || href.isEmpty()) {
        continue;
      }

      // Քաղում ենք վերնագիրը՝ մինչև ամսաթիվը
      String title = fullText.replaceAll("\\d{1,2}\\s+[а-яА-ЯёЁ]+(?:,\\s*\\d{4})?.*", "").trim();

      // Փորձում ենք քաղել ամսաթիվը
      LocalDate parsed = DateUtils.parseDateFromOtusText(fullText);

      if (parsed != null) {
        String startDateString = parsed.format(
            DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("ru")));
        courses.add(new CourseData(title, href, startDateString));
      } else {
        System.out.println("⚠️ Չհաջողվեց քաղել ամսաթիվը՝ " + fullText);
      }
    }

    return courses;
  }


  public void clickShowMoreUntilEnd() {
    int lastCount = 0;
    int stableTries = 0;

    while (stableTries < 3) {
      try {
        // ✅ Scroll to bottom
        ((JavascriptExecutor) driver).executeScript(
            "window.scrollTo(0, document.body.scrollHeight);");
        Thread.sleep(500); // Տալ DOM-ին բեռնվելու ժամանակ

        // ✅ Փորձում ենք գտնել "Показать ещё" կոճակը
        List<WebElement> buttons = driver.findElements(
            By.xpath("//button[contains(text(), 'Показать еще')]"));
        if (!buttons.isEmpty()) {
          WebElement button = buttons.get(0);
          if (button.isDisplayed() && button.isEnabled()) {
            System.out.println("🖱️ Կատարում ենք JavaScript click 'Показать ещё' կոճակի վրա");
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
            Thread.sleep(700); // տալիս ենք ավելի կայուն ժամանակ բեռնման համար
          }
        }

        // ✅ Վերցնում ենք կուրսերի քանակը
        List<WebElement> courses = driver.findElements(By.cssSelector("a[href^='/lessons/']"));
        int currentCount = courses.size();

        if (currentCount > lastCount) {
          System.out.println("📈 Նոր կուրսեր բեռնվեցին: " + currentCount);
          lastCount = currentCount;
          stableTries = 0;
        } else {
          stableTries++;
          System.out.println("⏸️ Քանակը չի փոխվել (փորձ " + stableTries + ")");
        }

      } catch (Exception e) {
        System.out.println("⚠️ Սխալ scroll-ի կամ կոճակի ժամանակ: " + e.getMessage());
        break;
      }
    }

    System.out.println("📦 Վերջնական բեռնված կուրսերի քանակը: " + lastCount);
  }


  public void waitForCourseToAppear(String courseName) {
    new WebDriverWait(driver, Duration.ofSeconds(10)).until(
        d -> driver.getPageSource().toLowerCase().contains(courseName.toLowerCase()));
  }

  public void debugPrintAllCourseTitles() {
    System.out.println("🔍 Կուրսերի վերնագրեր (debug):");
    getAllCoursesFromJsoup().forEach(course -> System.out.println("📘 " + course.title()));
  }

  public Optional<CourseData> findCourseByTitle(String targetTitle) {
    return getAllCoursesFromJsoup().stream()
        .filter(c -> c.title.toLowerCase().contains(targetTitle.toLowerCase())).findFirst();
  }

  public record CourseData(String title, String href, String startDate) {

  }
}
