package com.automation.center.models;

import lombok.Data;

@Data
public class Metric {
    private long id;
    private String name;
    private String code;
    private String jsonPath;
}
