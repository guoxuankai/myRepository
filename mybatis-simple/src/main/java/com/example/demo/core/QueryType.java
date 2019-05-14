package com.example.demo.core;


public enum QueryType {
    SELECT, UPDATE, INSERT, DELETE;

    public static QueryType value(String v) {
        return valueOf(v.toUpperCase());
    }
}
