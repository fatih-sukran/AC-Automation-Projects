package com.automation.center.lighthouse;

import org.testng.annotations.Test;


public class LighthouseTest {

    @Test
    public void lighthouseTest() {
        new LighthouseService().run();
    }
}
