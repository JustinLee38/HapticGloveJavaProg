package com.homelab;

public class Shape {
    private String name;
    private long aValue;
    private long bValue;
    private long cValue;
    private long dValue;

    public Shape(String name, long valueA, long valueB, long valueC, long valueD) {
        this.name = name;
        this.aValue = valueA;
        this.bValue = valueB;
        this.cValue = valueC;
        this.dValue = valueD;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getaValue() {
        return aValue;
    }

    public void setaValue(long aValue) {
        this.aValue = aValue;
    }

    public long getbValue() {
        return bValue;
    }

    public void setbValue(long bValue) {
        this.bValue = bValue;
    }

    public long getcValue() {
        return cValue;
    }

    public void setcValue(long cValue) {
        this.cValue = cValue;
    }

    public long getdValue() {
        return dValue;
    }

    public void setdValue(long dValue) {
        this.dValue = dValue;
    }

    @Override
    public String toString() {
        return "Shape{" +
                "name='" + name + '\'' +
                ", aValue=" + aValue +
                ", bValue=" + bValue +
                ", cValue=" + cValue +
                ", dValue=" + dValue +
                '}';
    }
}
