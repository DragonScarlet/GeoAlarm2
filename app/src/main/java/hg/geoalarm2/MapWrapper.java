package hg.geoalarm2;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

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
