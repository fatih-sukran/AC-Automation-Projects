package com.automation.center.lighthouse;

import com.automation.center.config.IConfig;
import com.automation.center.models.Metric;
import com.automation.center.models.Page;

import static io.restassured.RestAssured.*;

import com.automation.center.models.LighthouseResult;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

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

    private long createReport() {
        String endpoint = "/api/v1/report";
        var response = given()
            .contentType(ContentType.JSON)
            .body(new ReportRequest(suiteId, LocalDateTime.now().toString()))
        .when()
            .post(config.gatewayBaseUrl() + endpoint)
            .then()
            .extract()
            .jsonPath();
        return response.getLong("data.id");
    }

    private Pair<List<Page>, List<Metric>> getPageAndMetrics() {
        String endpoint = "/api/v1/suite/" + suiteId;
        var response = when()
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

    private void run(Page page) {
        var response = given()
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

    private void saveResult(LighthouseResult result) {
        var endpoint = "/api/v1/result";
        given()
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

