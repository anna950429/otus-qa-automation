package ru.otus.bdd.runner;

import com.google.inject.Inject;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.guice.ScenarioScoped;

import org.openqa.selenium.WebDriver;

/**
 * Եթե ունես setUp/tearDown լոգիկա, թող մնա; բայց WebDriver ստանում ենք DI‑ով,
 * ոչ թե static -ով։
 */
@ScenarioScoped
public class Hooks {

  private final WebDriver driver;

  @Inject
  public Hooks(WebDriver driver) {
    this.driver = driver;
  }

  @Before
  public void setUp() {
    // custom pre‑scenario steps (optional)
  }

  @After
  public void tearDown() {

  }
}
