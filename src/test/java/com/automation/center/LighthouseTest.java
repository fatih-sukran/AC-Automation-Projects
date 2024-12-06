package com.automation.center;

import com.automation.center.lighthouse.LighthouseService;
import org.testng.annotations.Test;

public class LighthouseTest {
    @Test
    public void lighthouseTest() {
        new LighthouseService().run();
    }
}
