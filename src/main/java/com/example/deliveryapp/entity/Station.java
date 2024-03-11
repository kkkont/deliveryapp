package com.example.deliveryapp.entity;

import javax.xml.bind.annotation.XmlElement;

public class Station {

    private String name;
    private String wmocode;
    private double airtemperature;
    private double windspeed;
    private String phenomenon;

    @XmlElement
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement
    public String getWmocode() {
        return wmocode;
    }

    public void setWmocode(String wmocode) {
        this.wmocode = wmocode;
    }
    @XmlElement
    public double getAirtemperature() {
        return airtemperature;
    }

    public void setAirtemperature(double airtemperature) {
        this.airtemperature = airtemperature;
    }
    @XmlElement
    public double getWindspeed() {
        return windspeed;
    }

    public void setWindspeed(double windspeed) {
        this.windspeed = windspeed;
    }
    @XmlElement
    public String getPhenomenon() {
        return phenomenon;
    }

    public void setPhenomenon(String phenomenon) {
        this.phenomenon = phenomenon;
    }
}
