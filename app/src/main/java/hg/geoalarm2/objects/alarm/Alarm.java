package hg.geoalarm2.objects.alarm;

import com.google.android.gms.maps.model.LatLng;

import hg.geoalarm2.objects.time.Date;
import hg.geoalarm2.objects.time.Time;
import hg.geoalarm2.objects.time.Week;

/**
 * Created by dimkn on 7/20/2017.
 */

public class Alarm {

    public enum type {
        SINGLE, MULTIPLE, AREA_ONLY
    }

    private String name;
    private Time startTime;
    private Time endTime;
    private Date startDay;
    private Date endDay;
    private int radius;
    private boolean active;
    private type type;
    private Week week;
    private double latitude;
    private double longitude;

    public Alarm(){
        this.name = "";
        this.startTime = null;
        this.endTime = null;
        this.startDay = null;
        this.endDay = null;
        this.radius = 0;
        this.active = false;
        this.type = null;
        this.week = null;
        this.latitude = 0;
        this.longitude = 0;
    }

    public Alarm(String name, Time startTime, Time endTime, Date startDay, Date endDay, int radius, boolean active, Alarm.type type, Week week, double latitude, double longitude) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startDay = startDay;
        this.endDay = endDay;
        this.radius = radius;
        this.active = active;
        this.type = type;
        this.week = week;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public Date getStartDay() {
        return startDay;
    }

    public void setStartDay(Date startDay) {
        this.startDay = startDay;
    }

    public Date getEndDay() {
        return endDay;
    }

    public void setEndDay(Date endDay) {
        this.endDay = endDay;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Alarm.type getType() {
        return type;
    }

    public void setType(Alarm.type type) {
        this.type = type;
    }

    public Week getWeek() {
        return week;
    }

    public void setWeek(Week week) {
        this.week = week;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public LatLng getLatLng(){
        return new LatLng(latitude, longitude);
    }
}
