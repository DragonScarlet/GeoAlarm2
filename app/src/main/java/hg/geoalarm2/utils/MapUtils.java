package hg.geoalarm2.utils;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by dimkn on 10/4/2017.
 */

public class MapUtils {

    public static String getMapKey(Marker marker) {
        if (marker != null) {
            return marker.getPosition().latitude + "###" + marker.getPosition().longitude;
        } else {
            return "";
        }
    }
}
