package com.example.dar.share;

public class AddTravelInformation {

    public String Origin;
    public String Destination;
    public Integer Available;
    public Integer NoOfUsers;
    public Integer FareFrom;
    public Integer FareTo;

    public AddTravelInformation(String origin, String destination, Integer available, Integer noOfUsers, Integer fareFrom, Integer fareTo){
        this.Origin = origin;
        this.Destination = destination;
        this.Available = available;
        this.NoOfUsers = noOfUsers;
        this.FareFrom = fareFrom;
        this.FareTo = fareTo;
    }

}
