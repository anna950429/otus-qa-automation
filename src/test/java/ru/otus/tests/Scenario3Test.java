package ru.otus.tests;

import com.google.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.otus.page.MainPage;
import ru.otus.page.CatalogPage;
import ru.otus.util.TestExtension;

@ExtendWith(TestExtension.class)
public class Scenario3Test {

   @Inject
   private MainPage mainPage;

   @Inject
   private CatalogPage catalogPage;

   @Test
   public void testRandomCategory() {
      // 1. Открываем главную страницу
      mainPage.open();

      // 2. Наводим курсор на «Обучение» (или можно и кликнуть, смотрите по сайту
      mainPage.openDropdownWithRetry();

      // 3. Случайным образом выбираем категорию
      String chosenCategory = mainPage.selectRandomCategory();

      // 4. Убеждаемся, что открылась страница каталога
      String currentUrl = mainPage.getCurrentUrl();
      Assertions.assertTrue(
          currentUrl.contains("categories"),
          "Не открылась страница категории! Текущий URL: " + currentUrl
      );

      // 5. Проверяем, что в ней есть хотя бы один курс
      Assertions.assertFalse(
          catalogPage.getAllCoursesFromJsoup().isEmpty(),
          "В выбранной категории («" + chosenCategory + "») нет ни одного курса!"
      );
   }
}
