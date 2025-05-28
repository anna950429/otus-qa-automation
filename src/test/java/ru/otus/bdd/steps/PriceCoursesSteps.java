package ru.otus.bdd.steps;

import io.cucumber.java.en.*;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import ru.otus.bdd.runner.Hooks;
import ru.otus.page.CatalogPage;
import ru.otus.page.CourseData; // Если нужен импорт конкретной модели

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Шаги (Steps) для работы со сценарием поиска
 * самого дорогого и самого дешёвого курса.
 */
public class PriceCoursesSteps {

  /**
   * Получаем WebDriver из Hooks (Guice).
   */
  private final WebDriver driver = Hooks.getDriver();

  /**
   * CatalogPage - ваш PageObject для работы со списком курсов
   * и кнопкой "Показать ещё".
   */
  private final CatalogPage catalogPage;

  /**
   * Храним список всех (подготовительных) курсов, загруженных на странице.
   */
  private List<CourseData> prepCourses;

  /**
   * Самый дорогой курс из списка.
   */
  private CourseData maxCourse;

  /**
   * Самый дешёвый курс из списка.
   */
  private CourseData minCourse;

  /**
   * В конструкторе получаем CatalogPage через Guice.
   */
  public PriceCoursesSteps() {
    // Hooks.getInjector() возвращает нам Injector,
    // из которого мы берём CatalogPage (где написана логика парсинга курсов).
    catalogPage = Hooks.getInjector().getInstance(CatalogPage.class);
  }

  /**
   * Шаг для перехода в нужный раздел (например, "Курсы" > "Подготовительные курсы").
   * В вашем случае можете либо использовать MainPage + Actions,
   * либо напрямую driver.get("https://otus.ru/categories/PodgotovitelnyeKursy/") и т.п.
   */
  @Given("я перехожу в раздел {string} > {string}")
  public void goToCoursesSection(String main, String sub) {
    System.out.println(">>> Переход в раздел: " + main + " > " + sub);
    catalogPage.open();
  }

  /**
   * Шаг, в котором мы предполагаем, что на странице есть кнопка "Показать ещё"
   * и все "Подготовительные курсы". После клика несколько раз они загружаются.
   * Затем мы собираем их данные (включая price).
   */
  @Given("я вижу список подготовительных курсов")
  public void iSeePrepCourses() {
    // Кликаем "Показать ещё" до тех пор, пока курсы продолжают подгружаться.
    catalogPage.clickShowMoreUntilEnd();

    // Получаем ВСЕ курсы со страницы с помощью Jsoup (метод getAllCoursesFromJsoup()).
    // ВАЖНО: внутри этого метода вы должны парсить цену и передавать её в CourseData.
    prepCourses = catalogPage.getAllCoursesFromJsoup();

    // Проверяем, что список не пуст.
    Assert.assertFalse("Нет подготовительных курсов!", prepCourses.isEmpty());
  }

  /**
   * Ищем курс с максимальной ценой.
   */
  @When("я выбираю самый дорогой курс")
  public void selectMaxPricedCourse() {
    // Если поле price в CourseData -> (c -> c.price())
    Optional<CourseData> maybeMax = prepCourses.stream()
        .max(Comparator.comparingInt(CourseData::price));

    Assert.assertTrue("Не найден самый дорогой курс!", maybeMax.isPresent());
    maxCourse = maybeMax.get();
  }

  /**
   * Ищем курс с минимальной ценой.
   */
  @When("выбираю самый дешевый курс")
  public void selectMinPricedCourse() {
    Optional<CourseData> maybeMin = prepCourses.stream()
        .min(Comparator.comparingInt(CourseData::price));

    Assert.assertTrue("Не найден самый дешевый курс!", maybeMin.isPresent());
    minCourse = maybeMin.get();
  }

  /**
   * Выводим в консоль информацию о самом дорогом и самом дешёвом курсе.
   */
  @Then("в консоли отображается название, дата и цена обоих курсов")
  public void printCoursesInfo() {
    System.out.println(">>> САМЫЙ ДОРОГОЙ КУРС: "
        + maxCourse.title()
        + " | дата: " + maxCourse.startDate()
        + " | цена: " + maxCourse.price());

    System.out.println(">>> САМЫЙ ДЕШЕВЫЙ КУРС: "
        + minCourse.title()
        + " | дата: " + minCourse.startDate()
        + " | цена: " + minCourse.price());
  }
}
