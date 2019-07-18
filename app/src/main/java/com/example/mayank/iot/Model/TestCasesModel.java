package com.example.mayank.iot.Model;

public class TestCasesModel {
    String count;
    int testNumber;

    public TestCasesModel(int testNumber2, String count2) {
        this.testNumber = testNumber2;
        this.count = count2;
    }

    public int getTestNumber() {
        return this.testNumber;
    }

    public String getCount() {
        return this.count;
    }
}
