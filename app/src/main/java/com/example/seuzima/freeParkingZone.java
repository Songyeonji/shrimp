package com.example.seuzima;

public class freeParkingZone {
    public String name;
    public String addr;
    public double lat;
    public double lon;
    public freeParkingZone(){
        //DataSnapshot.getValue(freeParkingZone.class) 호출에 필요한 기본 생성자
    }
    public freeParkingZone(String name, String addr, double lat, double lon){
        this.name = name;
        this.addr = addr;
        this.lat = lat;
        this.lon = lon;
    }
}
