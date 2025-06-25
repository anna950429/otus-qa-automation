package ru.otus.bdd.steps;

import com.google.inject.Inject;
import io.cucumber.java.en.*;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import ru.otus.bdd.runner.Hooks;
import ru.otus.page.CatalogPage;
import ru.otus.page.CourseData;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Шаги для проверки курсов, начинающихся не раньше определённой даты.
 */
public class StartDateSteps {

  private final WebDriver driver;

  /**
   * Page Object для каталога курсов
   */
  private final CatalogPage catalogPage;

  /**
   * Формат для парсинга даты из поля startDate (d MMMM yyyy, ru)
   */
  private final DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("ru"));

  /**
   * Полный список курсов (все, что загрузились через "Показать ещё")
   */
  private List<CourseData> allCourses;

  /**
   * Отфильтрованный список курсов (начинаются не раньше указанной даты)
   */
  private List<CourseData> filteredCourses;

  /**
   * В конструкторе получаем CatalogPage через Guice-инжектор (Hooks).
   */
  @Inject
  public StartDateSteps(WebDriver driver, CatalogPage catalogPage /* ... */) {
    this.driver = driver;
    this.catalogPage = catalogPage;
  }

  /**
   * Шаг: "я вижу список всех курсов"
   * - нажимает "Показать ещё" до конца,
   * - парсит список курсов (CourseData).
   */
  @Given("я вижу список всех курсов")
  public void iSeeAllCourses() {
    catalogPage.clickShowMoreUntilEnd();
    allCourses = catalogPage.getAllCoursesFromJsoup();

    Assert.assertFalse("Список курсов пуст!", allCourses.isEmpty());
  }

  /**
   * Шаг: "я фильтрую их, оставляя только те, что стартуют не раньше {string}"
   * - переводим {string} в LocalDate (dateLimit)
   * - отсекаем все курсы, у которых дата раньше этого предела.
   */
  @When("я фильтрую их, оставляя только те, что стартуют не раньше {string}")
  public void filterCoursesByDate(String dateStr) {
    LocalDate dateLimit = LocalDate.parse(dateStr);

    filteredCourses = allCourses.stream()
        .filter(c -> c.startDate() != null)
        .filter(c -> !c.startDate().isBefore(dateLimit))
        .toList();
  }


  /**
   * Шаг: "в консоли выводится информация (название, дата старта) об этих курсах"
   * - просто печатает отфильтрованные курсы.
   */
  @Then("в консоли выводится информация \\(название, дата старта\\) об этих курсах")
  public void printFilteredCourses() {
    for (CourseData c : filteredCourses) {
      System.out.println("КУРС: " + c.title() + "; ДАТА: " + c.startDate());
    }
  }

  /**
   * Шаг: "каждый из них действительно не раньше {string}"
   * - проверяем, что у каждого курса дата >= limit.
   */
  @And("каждый из них действительно не раньше {string}")
  public void verifyStartDate(String dateStr) {
    LocalDate limit = LocalDate.parse(dateStr);

    for (CourseData c : filteredCourses) {
      LocalDate start = c.startDate();
      Assert.assertFalse(
          "Курс " + c.title() + " начинается раньше " + limit,
          start.isBefore(limit)
      );
    }
  }
}
