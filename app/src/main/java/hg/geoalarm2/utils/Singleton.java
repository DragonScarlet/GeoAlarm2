package hg.geoalarm2.utils;

import android.app.Activity;
import android.app.PendingIntent;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;

import hg.geoalarm2.enums.State;
import hg.geoalarm2.objects.alarm.Alarm;

/**
 * Created by dimkn on 7/18/2017.
 */

public final class Singleton {

    private static volatile Singleton instance = null;
    private Singleton() {}
    private Marker currentMarker;
    private Alarm currentAlarm = new Alarm();
    private Alarm cacheAlarm = new Alarm();
    private State currentState;
    private Activity mainActivity;
    private HashMap<String, Alarm> alarmsMap;
    private HashMap<String, Geofence> geofenceMap = new HashMap<>();
    private Circle circle = null;


    // Geofence
    private GeofencingClient geofencingClient;
    private PendingIntent geofencePendingIntent;

    public GeofencingClient getGeofencingClient() {
        return geofencingClient;
    }

    public void setGeofencingClient(GeofencingClient geofencingClient) {
        this.geofencingClient = geofencingClient;
    }

    public PendingIntent getGeofencePendingIntent() {
        return geofencePendingIntent;
    }

    public void setGeofencePendingIntent(PendingIntent geofencePendingIntent) {
        this.geofencePendingIntent = geofencePendingIntent;
    }

    private CameraPosition oldCameraPosition;

    public static Singleton getInstance() {
        if (instance == null) {
            synchronized(Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }

    public Marker getCurrentMarker() {
        return currentMarker;
    }

    public void setCurrentMarker(Marker currentMarker) {
        this.currentMarker = currentMarker;
    }

    public CameraPosition getOldCameraPosition() {
        return oldCameraPosition;
    }

    public void setOldCameraPosition(CameraPosition oldCameraPosition) {
        this.oldCameraPosition = oldCameraPosition;
    }

    public Alarm getCurrentAlarm() {
        return currentAlarm;
    }

    public void setCurrentAlarm(Alarm currentAlarm) {
        this.currentAlarm = currentAlarm;
    }

    public State getCurrentState() {
        return currentState;
    }

    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }

    public Activity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(Activity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public HashMap<String, Alarm> getAlarmsMap() {
        return alarmsMap;
    }

    public void setAlarmsMap(HashMap<String, Alarm> alarmsMap) {
        this.alarmsMap = alarmsMap;
    }

    public Alarm getCacheAlarm() {
        return cacheAlarm;
    }

    public void setCacheAlarm(Alarm cacheAlarm) {
        this.cacheAlarm = cacheAlarm;
    }

    public HashMap<String, Geofence> getGeofenceMap() {
        return geofenceMap;
    }

    public void setGeofenceMap(HashMap<String, Geofence> geofenceMap) {
        this.geofenceMap = geofenceMap;
    }

    public Circle getCircle() {
        return circle;
    }

    public void setCircle(Circle circle) {
        this.circle = circle;
    }
}