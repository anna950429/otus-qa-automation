package ru.otus.tests;

import com.google.inject.Inject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.otus.page.CatalogPage;
import ru.otus.page.CourseData;
import ru.otus.page.CoursePage;
import ru.otus.util.TestExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ExtendWith(TestExtension.class)
public class Scenario2Test {

  @Inject
  private CatalogPage catalogPage;

  @Inject
  private CoursePage coursePage;

  @Test
  public void testEarliestAndLatestCourses() {
    // 1. Открываем каталог
    catalogPage.open();

    // 2. Нажимаем «Показать ещё» до конца, чтобы собрать все курсы
    catalogPage.clickShowMoreUntilEnd();

    // 3. Собираем все данные о курсах (title, href, startDate, price и т.п.)
    List<CourseData> allCourses = catalogPage.getAllCoursesFromJsoup();
    Assertions.assertFalse(allCourses.isEmpty(), "❌ Курс не найден в каталоге.");

    // ------------------------------
    // 4. Ищем самый ранний курс (через reduce)
    // ------------------------------
    Optional<CourseData> earliestCourseOpt = allCourses.stream()
        .filter(c -> c.startDate() != null)
        .reduce((c1, c2) -> c1.startDate().isBefore(c2.startDate()) ? c1 : c2);


    CourseData earliestCourse = earliestCourseOpt.orElseThrow(
        () -> new AssertionError("Не удалось найти самый ранний курс.")
    );
    LocalDate earliestDate = earliestCourse.startDate();

    // ------------------------------
    // 5. Ищем самый поздний курс (через reduce)
    // ------------------------------
    Optional<CourseData> latestCourseOpt = allCourses.stream()
        .filter(c -> c.startDate() != null)
        .reduce((c1, c2) -> c1.startDate().isAfter(c2.startDate()) ? c1 : c2);


    CourseData latestCourse = latestCourseOpt.orElseThrow(
        () -> new AssertionError("Не удалось найти самый поздний курс.")
    );
    LocalDate latestDate = latestCourse.startDate();

    System.out.println("Самый ранний курс: "
        + earliestCourse.title()
        + " | " + earliestCourse.startDate());
    System.out.println("Самый поздний курс: "
        + latestCourse.title()
        + " | " + latestCourse.startDate());

    // ------------------------------
    // 6. Собираем все курсы, которые начинаются либо в earliestDate, либо в latestDate
    // ------------------------------
    List<CourseData> boundaryCourses = allCourses.stream()
        .filter(c -> c.startDate() != null)
        .filter(c -> c.startDate().equals(earliestDate) || c.startDate().equals(latestDate))
        .collect(Collectors.toList());

    System.out.println("Всего курсов с граничными датами: " + boundaryCourses.size());

    // 7. Для каждого «граничного» курса открываем страницу и проверяем заголовок
    for (CourseData course : boundaryCourses) {
      openAndVerifyCourse(course);
    }
  }

  /**
   * Проверяем, что на странице правильно отображается название курса (через Jsoup).
   */
  private void openAndVerifyCourse(CourseData course) {
    String fullUrl = "https://otus.ru" + course.href();
    catalogPage.getDriver().get(fullUrl);

    String html = coursePage.getPageSource();
    Document doc = Jsoup.parse(html);

    // Ищем заголовок в <h1>
    String actualTitle = "";
    Element h1 = doc.selectFirst("h1");
    if (h1 != null) {
      actualTitle = h1.text();
    } else {
      // fallback на случай, если у курса другая верстка
      Element fallback = doc.selectFirst("div.sc-1ai2ech-8");
      if (fallback != null) {
        actualTitle = fallback.text();
      }
    }

    System.out.println("Проверяем курс: "
        + course.title()
        + " | дата: " + course.startDate());
    System.out.println("Открытая страница: " + actualTitle);

    // Сравниваем название с ожидаемым
    Assertions.assertTrue(
        actualTitle.toLowerCase().contains(course.title().toLowerCase())
            || course.title().toLowerCase().contains(actualTitle.toLowerCase()),
        String.format(
            "❌ Название не совпадает. Ожидалось: '%s', на странице: '%s'",
            course.title(),
            actualTitle
        )
    );
  }
}
