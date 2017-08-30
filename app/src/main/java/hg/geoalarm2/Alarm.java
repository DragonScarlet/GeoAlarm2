package hg.geoalarm2;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by dimkn on 7/20/2017.
 */

public class Alarm {

    public enum type {
        SINGLE, MULTIPLE, AREA_ONLY
    }

    public enum Weekdays {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }

    private String name;
    private Time startTime;
    private Time endTime;
    private Date startDay;
    private Date endDay;
    private float radius;
    private boolean active;
    private type type;
    private Weekdays[] weekdays;
    private double latitude;
    private double longitude;

    public Alarm(String name, Time startTime, Time endTime, Date startDay, Date endDay, float radius, boolean active, Alarm.type type, Weekdays[] weekdays, double latitude, double longitude) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startDay = startDay;
        this.endDay = endDay;
        this.radius = radius;
        this.active = active;
        this.type = type;
        this.weekdays = weekdays;
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

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
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

    public Weekdays[] getWeekdays() {
        return weekdays;
    }

    public void setWeekdays(Weekdays[] weekdays) {
        this.weekdays = weekdays;
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
