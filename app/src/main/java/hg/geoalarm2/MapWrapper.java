package hg.geoalarm2;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Created by dimkn on 8/27/2017.
 */

public class MapWrapper {
    private HashMap<String, Alarm> mMap;

    public HashMap<String, Alarm> getmMap() {
        return mMap;
    }

    public void setmMap(HashMap<String, Alarm> mMap) {
        this.mMap = mMap;
    }
}
