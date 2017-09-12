package hg.geoalarm2.utils;

import java.util.HashMap;

import hg.geoalarm2.objects.alarm.Alarm;

/**
 * Created by dimkn on 8/27/2017.
 */

public class MapWrapper {
    private HashMap<String, Alarm> mMap;

    public HashMap<String, Alarm> getMap() {
        return mMap;
    }

    public void setMap(HashMap<String, Alarm> mMap) {
        this.mMap = mMap;
    }
}
