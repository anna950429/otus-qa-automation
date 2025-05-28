package ru.otus.bdd.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.junit.Assert;
import ru.otus.bdd.runner.Hooks;
import ru.otus.util.WebDriverUnwrapUtils;

public class BrowserSteps {

  @When("я выбираю браузер {string}")
  public void iSelectBrowser(String browserName) {
    // Пример: если в вашем DriverModule уже жёстко захардкожен Chrome,
    // тут можете просто вывести лог,
    // или же можете как-то менять System.setProperty(...) и пересоздавать driver.
    System.out.println(">>> Псевдо-выбор браузера: " + browserName);
    // Если нужно реально подменять - придется передавать browserName в DriverModule
  }

  @Then("должно открыться окно Chrome")
  public void verifyChromeOpened() {
    WebDriver driver = Hooks.getDriver();
    WebDriver unwrapped = WebDriverUnwrapUtils.unwrap(driver);
    Assert.assertTrue("Ожидался ChromeDriver!",
        unwrapped instanceof ChromeDriver);
    System.out.println(">>> Проверка прошла: используем ChromeDriver");
  }
}
