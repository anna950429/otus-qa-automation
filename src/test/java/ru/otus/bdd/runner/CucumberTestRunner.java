package ru.otus.bdd.runner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "classpath:ru/otus/bdd/features",
    glue = {
        "ru.otus.bdd.steps",
        "ru.otus.bdd.runner"
    },
    plugin = {"pretty", "summary"},
    monochrome = true
)
public class CucumberTestRunner {
}
