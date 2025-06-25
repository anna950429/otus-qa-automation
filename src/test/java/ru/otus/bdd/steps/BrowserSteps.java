package ru.otus.bdd.steps;

import com.google.inject.Inject;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import ru.otus.util.WebDriverUnwrapUtils;

public class BrowserSteps {

  private final WebDriver driver;

  @Inject
  public BrowserSteps(WebDriver driver) {
    this.driver = driver;
  }

  @When("я выбираю браузер {string}")
  public void iSelectBrowser(String browserName) {
    System.out.println(">>> Выбран браузер из фичи: " + browserName);
    // Եթե ապագայում պետք է Firefox եւ այլն, կարող ես պահել browserName
    // somewhere in ThreadLocal context եւ օգտագործել DriverFactory‑ում
  }

  @Then("должно открыться окно Chrome")
  public void verifyChromeOpened() {
    Assert.assertTrue(
        WebDriverUnwrapUtils.unwrap(driver) instanceof ChromeDriver
    );
  }
}
