package com.example.dar.share;

public class AddUserInformation {
    public String FName;
    public String LName;
    public String Gender;
    public Integer Number;
    public Integer GuardianNumber;

    public AddUserInformation(String Fname, String Lname, String Gender, Integer Number, Integer GuardianNumber){
        this.FName = Fname;
        this.LName = Lname;
        this.Gender = Gender;
        this.Number = Number;
        this.GuardianNumber = GuardianNumber;
    }
}
