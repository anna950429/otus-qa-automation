package ru.otus.tests;

import com.google.inject.Inject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.otus.page.CatalogPage;
import ru.otus.page.CourseData;
import ru.otus.page.CoursePage;
import ru.otus.util.TestExtension;

@ExtendWith(TestExtension.class)
public class Scenario1Test {

  @Inject
  private CatalogPage catalogPage;

  @Inject
  private CoursePage coursePage;

  @Test
  public void testFindCourseByName() {
    String targetCourseName = "QA Automation Engineer";

    // Открываем страницу с результатами поиска
    catalogPage.openWithSearch(targetCourseName);

    // Ожидаем, пока элемент появится
    catalogPage.waitForCourseToAppear(targetCourseName);

    // Выводим debug-информацию
    catalogPage.debugPrintAllCourseTitles();

    // Ищем в списке
    CourseData course = catalogPage.findCourseByTitle(targetCourseName)
        .orElseThrow(() -> new AssertionError("Կուրսը չգտնվեց: " + targetCourseName));

    // Открываем страницу этого курса
    catalogPage.getDriver().get("https://otus.ru" + course.href());

    // Читаем заголовок с помощью Jsoup
    String html = coursePage.getPageSource();
    Document doc = Jsoup.parse(html);
    String actualTitle = doc.select("h1").first().text();

    // Проверяем, соответствует ли заголовок ожидаемому

    Assertions.assertTrue(actualTitle.contains(targetCourseName),
        "Пользователь открыл другую страницу. Ожидалось:  " + targetCourseName + ", а открылась:  "
            + actualTitle);
  }
}
