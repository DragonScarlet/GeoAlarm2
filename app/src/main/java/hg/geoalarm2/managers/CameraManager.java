package hg.geoalarm2.managers;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import hg.geoalarm2.utils.Singleton;

/**
 * Created by dimkn on 8/18/2017.
 */

public class CameraManager {

    private static final int TILT = 30;
    private static final int ZOOM = 50;


    public static void moveWithStyleToPosition(LatLng latLng, int radius, GoogleMap googleMap) {
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithStyle(latLng, radius)));
    }

    public static void zoomWithStyle(LatLng latLng, int progress, GoogleMap googleMap) {
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWhileZooming(latLng, progress)));
    }

    public static void resetCamera(GoogleMap googleMap) {
        if (Singleton.getInstance().getOldCameraPosition() != null) {
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(resetCameraPosition()));
        }
    }

    public static void moveToPosition(LatLng latLng, GoogleMap googleMap) {
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPosition(latLng, googleMap.getCameraPosition().zoom)));
    }

    private static CameraPosition getCameraPosition(LatLng latLng, float zoom){
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .zoom(zoom)
                .target(latLng)
                .build();
       return cameraPosition;
    }

    private static CameraPosition getCameraPositionWithStyle(LatLng latLng, int radius){
        CameraPosition cameraPosition
                = new CameraPosition.Builder()
                .target(latLng)
                .zoom(getZoom(radius))
                .tilt(TILT)
                .build();
        return cameraPosition;
    }

    private static CameraPosition getCameraPositionWhileZooming(LatLng latLng, int progress){
        if(progress == 0){
            progress++;
        }
        CameraPosition cameraPosition
                = new CameraPosition.Builder()
                .target(latLng)
                .zoom(getZoom(progress * ZOOM))
                .tilt(TILT)
                .build();
        return cameraPosition;
    }

    private static CameraPosition resetCameraPosition(){
        return Singleton.getInstance().getOldCameraPosition();
    }

    private static float getZoom(int radius){
        float r = (float) radius;
        return (float) (15 - Math.log(r / 500) / Math.log(2));
    }

}
