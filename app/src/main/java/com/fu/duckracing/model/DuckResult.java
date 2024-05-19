package com.fu.duckracing.model;

public class DuckResult {
    private final String duckName;
    private final int time;

    public DuckResult(String duckName, int time) {
        this.duckName = duckName;
        this.time = time;
    }

    public String getDuckName() {
        return duckName;
    }

    public int getTime() {
        return time;
    }
}
