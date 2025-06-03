package ru.otus.citrus;

import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.dsl.runner.TestRunner;

public class CourseApiMockTest {

    @CitrusTest
    public void mockCourseApi(@CitrusResource TestRunner runner) {
        runner.http(builder -> builder.server("courseApiServer")
            .receive()
            .get("/api/courses"));

        runner.http(builder -> builder.server("courseApiServer")
            .send()
            .response()
            .payload("""
                [
                  {
                    "id": 1,
                    "name": "QA Automation Engineer",
                    "startDate": "2025-06-01"
                  }
                ]
                """)
            .contentType("application/json"));
    }
}
