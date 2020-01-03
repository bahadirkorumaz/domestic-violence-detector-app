package com.agu.domesticviolencedetector;

public class User {
    String name, email;
    int age, gender, dangerLevel;


    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getDangerLevel() {
        return dangerLevel;
    }

    public void setDangerLevel(int dangerLevel) {
        this.dangerLevel = dangerLevel;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public User(String name, String email, int age, int gender, int dangerLevel) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.gender = gender;
        this.dangerLevel = dangerLevel;
    }
}
