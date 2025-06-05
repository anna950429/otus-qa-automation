package ru.otus.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtils {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("d MMMM yyyy",
      new Locale("ru"));

  private DateUtils() {
  }

  public static LocalDate parseDateFromOtusText(String text) {
    // Уникальный шаблон для дня, месяца и года
    Pattern pattern = Pattern.compile("(\\d{1,2})\\s([а-яА-ЯёЁ]+)(?:,\\s?(\\d{4}))?");
    Matcher matcher = pattern.matcher(text);

    if (matcher.find()) {
      String day = matcher.group(1);
      String month = matcher.group(2);
      String year =
          matcher.group(3) != null ? matcher.group(3) : String.valueOf(LocalDate.now().getYear());

      String fullDate = day + " " + month + " " + year;

      try {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("ru"));
        return LocalDate.parse(fullDate, formatter);
      } catch (DateTimeParseException e) {
        System.out.println("⚠️Не удалось декодировать дату: " + fullDate);
        return null;
      }
    }

    // Если не удалось найти ни день, ни месяц

    return null;
  }


}
