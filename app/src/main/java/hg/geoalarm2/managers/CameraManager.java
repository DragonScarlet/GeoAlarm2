package hg.geoalarm2.managers;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import hg.geoalarm2.utils.Singleton;

/**
 * Created by dimkn on 8/18/2017.
 */

public class CameraManager {

    public static CameraPosition getCameraPosition(LatLng latLng, float zoom){
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .zoom(zoom)
                .target(latLng)
                .build();
       return cameraPosition;
    }

    public static CameraPosition getCameraPositionWithStyle(LatLng latLng, int radius){
        CameraPosition cameraPosition
                = new CameraPosition.Builder()
                .target(latLng)
                .zoom(getZoom(radius))
                .tilt(30)
                .build();
        return cameraPosition;
    }

    public static CameraPosition getCameraPositionWhileZooming(LatLng latLng, int progress){
        if(progress == 0){
            progress++;
        }
        CameraPosition cameraPosition
                = new CameraPosition.Builder()
                .target(latLng)
                .zoom(getZoom(progress * 50))
                .tilt(30)
                .build();
        return cameraPosition;
    }

    public static CameraPosition resetCameraPosition(){
        return Singleton.getInstance().getOldCameraPosition();
    }

    private static float getZoom(int radius){
        float r = (float) radius;
        return (float) (15 - Math.log(r / 500) / Math.log(2));
    }

}
