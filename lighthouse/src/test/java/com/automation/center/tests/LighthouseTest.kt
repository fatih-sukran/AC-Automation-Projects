package com.automation.center.tests

import com.automation.center.lighthouse.LighthouseService
import org.testng.annotations.Test

class LighthouseTest {
    @Test
    fun lighthouseTest() {
        LighthouseService().run()
    }
}
