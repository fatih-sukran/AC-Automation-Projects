package com.automation.center.models;

import lombok.Data;

@Data
public class Page {
    private long id;
    private String url;
    private String name;
    private long suiteId;
}
