package com.example.seuzima;

public class noParkingZone {
    public String name;
    public String addr;
    public double lat;
    public double lon;
    public noParkingZone(){
        //DataSnapshot.getValue(noParkingZone.class) 호출에 필요한 기본 생성자
    }
    public noParkingZone(String name, String addr, double lat, double lon){
        this.name = name;
        this.addr = addr;
        this.lat = lat;
        this.lon = lon;
    }
}
