package ru.otus.bdd.steps;

import com.google.inject.Inject;
import io.cucumber.java.en.*;
import org.junit.Assert;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;
import ru.otus.bdd.runner.Hooks;
import ru.otus.page.CatalogPage;

import java.util.List;
import java.util.Random;
import ru.otus.page.CourseData;

public class CourseSearchSteps {

  private final WebDriver driver;
  private final CatalogPage catalogPage;
  private String targetCourseName;


  @Inject
  public CourseSearchSteps(WebDriver driver, CatalogPage catalogPage /* ... */) {
    this.driver = driver;
    this.catalogPage = catalogPage;
  }

  @Given("я открываю страницу {string}")
  public void openPage(String url) {
    driver.get(url);
  }

  @And("я ввожу название курса {string}")
  public void enterCourseName(String courseName) {
    this.targetCourseName = courseName;
  }

  @When("я нажимаю поиск")
  public void clickSearch() {
    // Если вам нужно нажать на поле поиска, сделайте это.
    // Или, как в вашем коде, можно просто использовать openWithSearch(query).
    catalogPage.openWithSearch(targetCourseName);
  }

  @When("я случайно выбираю один из найденных курсов")
  public void chooseRandomCourse() {
    // В вашем случае findCourseByTitle находит только один.
    // Но, допустим, мы хотим взять все курсы, у которых в названии строка targetCourseName
    List<CourseData> courses = catalogPage.getAllCoursesFromJsoup();
    List<CourseData> matching = courses.stream()
        .filter(c -> c.title().toLowerCase().contains(targetCourseName.toLowerCase()))
        .toList();

    Assert.assertFalse("Список подходящих курсов пуст!", matching.isEmpty());

    // Случайно берём один
    CourseData chosen = matching.get(new Random().nextInt(matching.size()));
    String fullUrl = "https://otus.ru" + chosen.href();
    driver.get(fullUrl);
  }

  @Then("я вижу страницу курса, соответствующую {string}")
  public void iSeeCoursePage(String expectedName) {
    String html = driver.getPageSource();
    Document doc = Jsoup.parse(html);
    String actualTitle = doc.select("h1").text();
    System.out.println("Страница: " + actualTitle);

    Assert.assertTrue(
        "Ожидалось, что заголовок содержит: " + expectedName,
        actualTitle.contains(expectedName)
    );
  }
}
