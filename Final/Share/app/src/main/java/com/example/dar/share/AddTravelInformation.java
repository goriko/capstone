package com.example.dar.share;

import com.google.android.gms.maps.model.LatLng;

public class AddTravelInformation {

    public LatLng Origin;
    public LatLng Destination;
    public String OriginString;
    public String DestinationString;
    public Integer Available;
    public Integer NoOfUsers;
    public Integer FareFrom;
    public Integer FareTo;
    public Integer DepartureHour;
    public Integer DepartureMinute;
    public Integer EstimatedTravelTime;

    public AddTravelInformation(LatLng origin, LatLng destination, String originString, String destinationString, Integer available, Integer noOfUsers, Integer fareFrom, Integer fareTo, Integer departureHour, Integer departureMinute, Integer estimatedTravelTime){
        this.Origin = origin;
        this.Destination = destination;
        this.OriginString = originString;
        this.DestinationString = destinationString;
        this.Available = available;
        this.NoOfUsers = noOfUsers;
        this.FareFrom = fareFrom;
        this.FareTo = fareTo;
        this.DepartureHour = departureHour;
        this.DepartureMinute = departureMinute;
        this.EstimatedTravelTime = estimatedTravelTime;
    }

}
