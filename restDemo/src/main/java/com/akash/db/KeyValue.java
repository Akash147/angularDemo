package com.akash.db;

public class KeyValue {
    private String key;
    private String value;
    private Long lastUpdated;


    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getValue() {
        return value;// value.toString();
    }
    public void setValue(String value) {
        this.value = value;
    }
    public Long getLastUpdated() {
        return lastUpdated;
    }
    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }


}
