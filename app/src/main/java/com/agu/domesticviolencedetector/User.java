package com.agu.domesticviolencedetector;

import java.io.Serializable;

public class User implements Serializable {
    String name, email, userId;
    int age, gender, dangerLevel;

    int nWeek,nMonth,n3Month,n6Month, nAll;
    double rWeek,rMonth,r3Month,r6Month, rAll;

    String emergencyContact1,emergencyContact2,emergencyContact3;
    String location;
    int ageS,educationS,alcoholS,threatenS,welfareS,blameS,violenceS;
    boolean isRegitrationComplete=false;

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

    public String getEmergencyContact1() {
        return emergencyContact1;
    }

    public void setEmergencyContact1(String emergencyContact1) {
        this.emergencyContact1 = emergencyContact1;
    }

    public String getEmergencyContact2() {
        return emergencyContact2;
    }

    public void setEmergencyContact2(String emergencyContact2) {
        this.emergencyContact2 = emergencyContact2;
    }

    public String getEmergencyContact3() {
        return emergencyContact3;
    }

    public void setEmergencyContact3(String emergencyContact3) {
        this.emergencyContact3 = emergencyContact3;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getAgeS() {
        return ageS;
    }

    public void setAgeS(int ageS) {
        this.ageS = ageS;
    }

    public boolean isRegitrationComplete() {
        return isRegitrationComplete;
    }

    public void setRegitrationComplete(boolean regitrationComplete) {
        isRegitrationComplete = regitrationComplete;
    }

    public int getEducationS() {
        return educationS;
    }

    public void setEducationS(int educationS) {
        this.educationS = educationS;
    }

    public int getAlcoholS() {
        return alcoholS;
    }

    public void setAlcoholS(int alcoholS) {
        this.alcoholS = alcoholS;
    }

    public int getThreatenS() {
        return threatenS;
    }

    public void setThreatenS(int threatenS) {
        this.threatenS = threatenS;
    }

    public int getWelfareS() {
        return welfareS;
    }

    public void setWelfareS(int welfareS) {
        this.welfareS = welfareS;
    }

    public int getBlameS() {
        return blameS;
    }

    public void setBlameS(int blameS) {
        this.blameS = blameS;
    }

    public int getViolenceS() {
        return violenceS;
    }

    public void setViolenceS(int violenceS) {
        this.violenceS = violenceS;
    }

    public int getnWeek() {
        return nWeek;
    }

    public void setnWeek(int nWeek) {
        this.nWeek = nWeek;
    }

    public int getnMonth() {
        return nMonth;
    }

    public void setnMonth(int nMonth) {
        this.nMonth = nMonth;
    }

    public int getN3Month() {
        return n3Month;
    }

    public void setN3Month(int n3Month) {
        this.n3Month = n3Month;
    }

    public int getN6Month() {
        return n6Month;
    }

    public void setN6Month(int n6Month) {
        this.n6Month = n6Month;
    }

    public double getrWeek() {
        return rWeek;
    }

    public void setrWeek(double rWeek) {
        this.rWeek = rWeek;
    }

    public double getrMonth() {
        return rMonth;
    }

    public void setrMonth(double rMonth) {
        this.rMonth = rMonth;
    }

    public double getR3Month() {
        return r3Month;
    }

    public void setR3Month(double r3Month) {
        this.r3Month = r3Month;
    }

    public double getR6Month() {
        return r6Month;
    }

    public void setR6Month(double r6Month) {
        this.r6Month = r6Month;
    }

    public int getnAll() {
        return nAll;
    }

    public void setnAll(int nAll) {
        this.nAll = nAll;
    }

    public double getrAll() {
        return rAll;
    }

    public void setrAll(double rAll) {
        this.rAll = rAll;
    }

    public User(String name, String email, int age, int gender) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.age = age;
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
