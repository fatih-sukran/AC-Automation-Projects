package com.automation.center.models

class Metric() {
    var id: Long = 0
    var name: String = ""
    var code: String = ""
    var jsonPath: String = ""

    constructor(id: Long, name: String, code: String, jsonPath: String) : this() {
        this.id = id
        this.name = name
        this.code = code
        this.jsonPath = jsonPath
    }

    override fun toString(): String {
        return "Metric{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
