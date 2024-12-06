package com.automation.center.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LighthouseResult {
    private Page page;
    private Metric metric;
    private String value;
}

