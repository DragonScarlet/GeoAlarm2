package hg.geoalarm2;

import com.google.android.gms.maps.model.Marker;

import java.time.MonthDay;
import java.util.Date;

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
    private Date startTime;
    private Date endTime;
    private float radius;
    private boolean active;
    private type type;
    private Weekdays[] weekdays;

    public Alarm(String name, Date startTime, Date endTime, float radius, boolean active, type type, Weekdays[] weekdays) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.radius = radius;
        this.active = active;
        this.type = type;
        this.weekdays = weekdays;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
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
}
