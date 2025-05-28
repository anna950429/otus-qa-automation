package ru.otus.tests;

import com.google.inject.Inject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.otus.page.CatalogPage;
import ru.otus.page.CoursePage;
import ru.otus.page.CatalogPage.CourseData;
import ru.otus.util.TestExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@ExtendWith(TestExtension.class)
public class Scenario2Test {

   @Inject
   private CatalogPage catalogPage;

   @Inject
   private CoursePage coursePage;

   private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("ru"));

   @Test
   public void testEarliestAndLatestCourses() {
      // 1. Открываем каталог
      catalogPage.open();

      // 2. Нажимаем «Показать ещё» до конца, чтобы собрать все курсы
      catalogPage.clickShowMoreUntilEnd();

      // 3. Собираем данные всех курсов (название, ссылка, дата в виде строки)
      List<CourseData> allCourses = catalogPage.getAllCoursesFromJsoup();

      Assertions.assertFalse(allCourses.isEmpty(), "❌ Курс не найден в каталоге.");

      // ------------------------------
      // 4. Ищем самый ранний курс (через reduce)
      // ------------------------------
      Optional<CourseData> earliestCourseOpt = allCourses.stream()
          .reduce((c1, c2) -> {
             LocalDate d1 = LocalDate.parse(c1.startDate(), formatter);
             LocalDate d2 = LocalDate.parse(c2.startDate(), formatter);
             // если d1 раньше, то возвращаем c1, иначе c2
             return d1.isBefore(d2) ? c1 : c2;
          });

      CourseData earliestCourse = earliestCourseOpt.orElseThrow(
          () -> new AssertionError("Не удалось найти самый ранний курс.")
      );
      // Парсим дату у «самого раннего»:
      LocalDate earliestDate = LocalDate.parse(earliestCourse.startDate(), formatter);

      // ------------------------------
      // 5. Ищем самый поздний курс (через reduce)
      // ------------------------------
      Optional<CourseData> latestCourseOpt = allCourses.stream()
          .reduce((c1, c2) -> {
             LocalDate d1 = LocalDate.parse(c1.startDate(), formatter);
             LocalDate d2 = LocalDate.parse(c2.startDate(), formatter);
             // если d1 позже, то возвращаем c1, иначе c2
             return d1.isAfter(d2) ? c1 : c2;
          });

      CourseData latestCourse = latestCourseOpt.orElseThrow(
          () -> new AssertionError("Не удалось найти самый поздний курс.")
      );
      // Парсим дату у «самого позднего»:
      LocalDate latestDate = LocalDate.parse(latestCourse.startDate(), formatter);

      System.out.println("Самый ранний курс: " + earliestCourse.title() + " | " + earliestCourse.startDate());
      System.out.println("Самый поздний курс: " + latestCourse.title() + " | " + latestCourse.startDate());

      // ------------------------------
      // 6. Собираем все курсы, которые начинаются либо в earliestDate, либо в latestDate
      // ------------------------------
      List<CourseData> boundaryCourses = allCourses.stream()
          .filter(c -> {
             LocalDate d = LocalDate.parse(c.startDate(), formatter);
             return d.equals(earliestDate) || d.equals(latestDate);
          })
          .collect(Collectors.toList());

      System.out.println("Всего курсов с граничными датами: " + boundaryCourses.size());

      // 7. Для каждого «граничного» курса открываем его страницу и проверяем заголовок
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
         // fallback на случай, если у курса другой шаблон верстки
         Element fallback = doc.selectFirst("div.sc-1ai2ech-8");
         if (fallback != null) {
            actualTitle = fallback.text();
         }
      }

      System.out.println("Проверяем курс: " + course.title() + " | дата: " + course.startDate());
      System.out.println("Открытая страница: " + actualTitle);

      // Сравниваем название в карточке с тем, что мы ожидали
      Assertions.assertTrue(
          actualTitle.toLowerCase().contains(course.title().toLowerCase()) ||
              course.title().toLowerCase().contains(actualTitle.toLowerCase()),
          String.format(
              "❌ Название не совпадает. Ожидалось: '%s', на странице: '%s'",
              course.title(), actualTitle
          )
      );
   }
}
