package hg.geoalarm2;

import android.app.Activity;
import android.view.View;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;

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
}