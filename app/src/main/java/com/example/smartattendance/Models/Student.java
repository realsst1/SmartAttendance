package com.example.smartattendance.Models;

public class Student {

    private String name;
    private String roll;
    private String imageURL;

    public Student(String name, String roll) {
        this.name = name;
        this.roll = roll;
    }


    public String getRoll() {
        return roll;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }

    public Student() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
