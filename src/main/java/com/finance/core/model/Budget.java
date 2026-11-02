package com.finance.core.model;

public class Budget {
    private final String category;
    private double limit;

    public Budget(String category, double limit) {
        this.category = category;
        this.limit = limit;
    }

    public String getCategory() { return category; }
    public double getLimit() { return limit; }
    public void setLimit(double limit) { this.limit = limit; }
}