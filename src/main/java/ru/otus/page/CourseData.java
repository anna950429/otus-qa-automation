package ru.otus.page;

import java.time.LocalDate;

/**
 * Модель данных для курса.
 *
 * @param title     Название курса
 * @param href      Ссылка (относительная) на страницу курса
 * @param startDate Дата начала курса
 * @param price     Стоимость курса
 */
public record CourseData(String title,
                         String href,
                         LocalDate startDate,
                         int price) {
}
