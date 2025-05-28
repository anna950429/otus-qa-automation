package ru.otus.tests;

import com.google.inject.Inject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.otus.page.CatalogPage;
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

      // Բացում ենք որոնված էջը՝
      catalogPage.openWithSearch(targetCourseName);

      // Սպասում ենք՝ մինչև հայտնվի
      catalogPage.waitForCourseToAppear(targetCourseName);

      // Տպում ենք debug տվյալները
      catalogPage.debugPrintAllCourseTitles();

      // Փնտրում ենք ցանկից
      CatalogPage.CourseData course = catalogPage.findCourseByTitle(targetCourseName)
          .orElseThrow(() -> new AssertionError("Կուրսը չգտնվեց: " + targetCourseName));

      // Բացում ենք հենց այդ կուրսի էջը
      catalogPage.getDriver().get("https://otus.ru" + course.href());

      // Կարդում ենք վերնագիրը Jsoup-ով
      String html = coursePage.getPageSource();
      Document doc = Jsoup.parse(html);
      String actualTitle = doc.select("h1").first().text();


      // Վավերացնում ենք՝ արդյոք վերնագիրը համապատասխանում է

      Assertions.assertTrue(
          actualTitle.contains(targetCourseName),
          "Օգտատերը բացել է այլ էջ։ Սպասվում էր՝ " + targetCourseName + ", իսկ բացվեց՝ " + actualTitle
      );
   }
}
