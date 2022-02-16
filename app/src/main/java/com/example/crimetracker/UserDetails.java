package com.example.crimetracker;

public class UserDetails {
    public String name , email, contactNumber, emergencyNum1, emergencyNum2, password;

    public UserDetails(){}

    public UserDetails(String name , String email, String contactNumber, String emergencyNum1, String emergencyNum2, String password){
        this.name = name;
        this.email = email;
        this.contactNumber = contactNumber;
        this.emergencyNum1 = emergencyNum1;
        this.emergencyNum2 = emergencyNum2;
        this.password = password;
    }
}
