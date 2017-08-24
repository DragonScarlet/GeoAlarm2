package hg.geoalarm2;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by dimkn on 7/18/2017.
 */

public final class Singleton {
    private static volatile Singleton instance = null;

    private Singleton() {}
    private Marker currentMarker;


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
}