package com.automation.center.lighthouse;

import com.automation.center.config.IConfig;
import com.automation.center.models.LighthouseResult;
import com.automation.center.models.Metric;
import com.automation.center.models.Page;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;

public class LighthouseService {
    private final IConfig config = IConfig.getConfig();
    private final long suiteId = config.suiteId();
    private final long reportId = createReport();
    private final List<Page> pages;
    private final List<Metric> metrics;

    public LighthouseService() {
        Pair<List<Page>, List<Metric>> pair = getPageAndMetrics();
        this.pages = pair.first();
        this.metrics = pair.second();
    }

    @Step("Create Report")
    private long createReport() {
        String endpoint = "/api/v1/report";
        var response = given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .body(new ReportRequest(suiteId, LocalDateTime.now().toString()))
                .when()
                .post(config.gatewayBaseUrl() + endpoint)
                .then()
                .extract()
                .jsonPath();
        return response.getLong("data.id");
    }

    @Step("Get Page and Metrics")
    private Pair<List<Page>, List<Metric>> getPageAndMetrics() {
        String endpoint = "/api/v1/suite/" + suiteId;
        var response = given()
                .filter(new AllureRestAssured())
                .when()
                .get(config.gatewayBaseUrl() + endpoint)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .jsonPath();

        List<Page> pages = response.getList("data.pages", Page.class);
        List<Metric> metrics = response.getList("data.metrics", Metric.class);
        return new Pair<>(pages, metrics);
    }

    public void run() {
        for (Page page : pages) {
            run(page);
        }
    }

    @Step("Run Lighthouse")
    private void run(Page page) {
        var response = given()
                .filter(new AllureRestAssured())
                .queryParam("url", page.getUrl())
                .queryParam("strategy", "mobile")
                .queryParam("category", "performance")
                .when()
                .get(config.lighthouseUrl())
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .jsonPath();

        for (Metric metric : metrics) {
            Object value = response.get(metric.getJsonPath());
            LighthouseResult result = new LighthouseResult(page, metric, value.toString());
            saveResult(result);
        }
    }

    @Step("Save Result")
    private void saveResult(LighthouseResult result) {
        var endpoint = "/api/v1/result";
        given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .body(new ResultRequest(reportId, result.getPage().getId(), result.getMetric().getId(), result.getValue()))
                .when()
                .post(config.gatewayBaseUrl() + endpoint)
                .then()
                .statusCode(HttpStatus.SC_CREATED);
    }

    private record ReportRequest(long suiteId, String date) {
    }

    private record ResultRequest(long reportId, long pageId, long metricId, String value) {
    }

    private record Pair<F, S>(F first, S second) {
    }
}

