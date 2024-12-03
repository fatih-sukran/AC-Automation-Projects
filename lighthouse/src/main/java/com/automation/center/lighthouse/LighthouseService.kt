package com.automation.center.lighthouse

import com.automation.center.config.IConfig
import com.automation.center.models.Metric
import com.automation.center.models.Page
import com.automation.center.models.Result
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.apache.http.HttpStatus
import java.time.LocalDateTime

class LighthouseService {
    private val suiteId: Long
    private val reportId: Long
    private val pages: List<Page>
    private val metrics: List<Metric>
    private val config = IConfig.getConfig()

    init {
        this.suiteId = config.suiteId()
        this.reportId = createReport()
        val (pages, metrics) = getPageAndMetrics()
        this.pages = pages
        this.metrics = metrics
    }

    private fun createReport(): Long {
        val endpoint = "/api/v1/report"
        val response = Given {
            contentType(ContentType.JSON)
            body(object {
                val suiteId = this@LighthouseService.suiteId // Use the parent class's suiteId
                val date = LocalDateTime.now().toString() // Capture current date-time
            })
        } When {
            post(config.gatewayBaseUrl() + endpoint)
        } Extract {
            jsonPath()
        }
        return response["data.id"]
    }

    private fun getPageAndMetrics(): Pair<List<Page>, List<Metric>> {
        val endpoint = "/api/v1/suite/$suiteId"
        val response = When {
            get(config.gatewayBaseUrl() + endpoint)
        } Then {
            statusCode(HttpStatus.SC_OK)
        } Extract {
            jsonPath()
        }

        val pages = response.getList("data.pages", Page::class.java)
        val metrics = response.getList("data.metrics", Metric::class.java)
        return Pair(pages, metrics)
    }

    fun run() {
        pages.forEach {
            run(it)
        }
    }

    private fun run(page: Page) {
        val response = Given {
            queryParam("url", page.url)
            queryParam("strategy", "mobile")
            queryParam("category", "performance")
        } When {
            get(config.lighthouseUrl())
        } Then {
            statusCode(HttpStatus.SC_OK)
        } Extract {
            jsonPath()
        }

        metrics.forEach() {
            val value: Any = response[it.jsonPath]
            val result = Result(page, it, value.toString())
            saveResult(result)
        }
    }

    private fun saveResult(result: Result) {
        val endpoint = "/api/v1/result"
        Given {
            contentType(ContentType.JSON)
            body(object {
                val reportId = this@LighthouseService.reportId
                val pageId = result.page.id
                val metricId = result.metric.id
                val value = result.value
            })
        } When {
            post(config.gatewayBaseUrl() + endpoint)
        } Then {
            statusCode(HttpStatus.SC_CREATED)
        }
    }
}
