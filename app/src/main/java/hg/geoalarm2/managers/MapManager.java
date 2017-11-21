package hg.geoalarm2.managers;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import hg.geoalarm2.utils.MapUtils;
import hg.geoalarm2.utils.Singleton;

import static hg.geoalarm2.enums.State.NO_MARKER;

/**
 * Created by dimkn on 10/4/2017.
 */

public class MapManager {

    final static int STROKE_COLOR = 0x22000FF;
    final static int FILL_COLOR = 0x22000FF;

    public static void paintCircle(int radius, GoogleMap googleMap) {
        removeCircle();
        Singleton.getInstance().setCircle(googleMap.addCircle(new CircleOptions()
                .center(Singleton.getInstance().getCurrentMarker().getPosition())
                .radius(radius)
                .strokeColor(STROKE_COLOR)
                .fillColor(FILL_COLOR)));
    }

    public static void removeCircle() {
        if (Singleton.getInstance().getCircle() != null) {
            Singleton.getInstance().getCircle().remove();
        }
    }

    public static void removeTempMarker(GoogleMap googleMap) {
        if (Singleton.getInstance().getCurrentMarker() != null &&
                !Singleton.getInstance().getAlarmsMap().containsKey(MapUtils.getMapKey(Singleton.getInstance().getCurrentMarker()))) {
            removeCurrentMarker(googleMap);
        }
    }

    public static void createMarker(LatLng point, GoogleMap googleMap) {
        Marker tmp = googleMap.addMarker(new MarkerOptions().position(point).title("Create New Alarm"));
        Singleton.getInstance().setCurrentMarker(tmp);
    }

    public static void removeCurrentMarker(GoogleMap googleMap) {
        if (Singleton.getInstance().getCurrentMarker() != null) {
            Singleton.getInstance().getAlarmsMap().remove(MapUtils.getMapKey(Singleton.getInstance().getCurrentMarker()));
            Singleton.getInstance().getCurrentMarker().remove();
            Singleton.getInstance().setCurrentMarker(null);
            DataManager.saveAlarms();
        }
        Singleton.getInstance().setCurrentState(NO_MARKER);
        CameraManager.resetCamera(googleMap);
    }
}
