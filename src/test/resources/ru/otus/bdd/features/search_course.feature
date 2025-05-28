Feature: Поиск курса по названию
  Как пользователь
  Хочу найти курс по имени и открыть один из вариантов
  Чтобы убедиться, что страница верная

  Scenario: Поиск курса "QA Automation Engineer"
    Given я открываю страницу "https://otus.ru/catalog/courses"
    And я ввожу название курса "QA Automation Engineer"
    When я нажимаю поиск
    And я случайно выбираю один из найденных курсов
    Then я вижу страницу курса, соответствующую "QA Automation Engineer"
