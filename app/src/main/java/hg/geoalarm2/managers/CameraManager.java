package hg.geoalarm2.managers;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import hg.geoalarm2.utils.Singleton;

/**
 * Created by dimkn on 8/18/2017.
 */

public class CameraManager {


    public static CameraPosition getCameraPosition(LatLng latLng, float zoom){
        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .zoom(zoom)
                .target(latLng)      // Sets the center of the map to Mountain View
                .build();                   // Creates a CameraPosition from the builder
       return cameraPosition;
    }

    public static CameraPosition getCameraPositionWithStyle(LatLng latLng, int radius){
        //double offset = 0.0015;
        //LatLng latLng1 = new LatLng(latLng.latitude, latLng.longitude - offset);
        float rad = (float) radius;
        float zoomLevel = (float) (15 - Math.log(rad / 500) / Math.log(2));

        CameraPosition cameraPosition
                = new CameraPosition.Builder()
                .target(latLng)      // Sets the center of the map to Mountain View
                .zoom(zoomLevel)                   // Sets the zoom
                .tilt(30)
                .build();                   // Creates a CameraPosition from the builder
        return cameraPosition;
    }

    public static CameraPosition getCameraPositionWhileZooming(LatLng latLng, int progress){

        double minOffSet = 0.0015;
        double maxOffSet = 0.0900;
        float zoomMinOffSet = 11F;
        float zoomMaxOffSet = 17F;
        float maxProgress = 100F;

/*
        if(progress <= 10){
            minOffSet = 0.00075;
            maxOffSet = 0.0090;*5
            zoomMinOffSet = 14F;
            zoomMaxOffSet = 18F;
            maxProgress = 10;
        }

        if(progress <= 4){
            minOffSet = 0.00075;
            maxOffSet = 0.00300;
            zoomMinOffSet = 16F;
            zoomMaxOffSet = 18F;
            maxProgress = 4;
        }

*/
       // double lonOffset = minOffSet + (((maxOffSet - minOffSet) / maxProgress) * progress);
        //LatLng latLng1 = new LatLng(latLng.latitude, latLng.longitude - lonOffset);
       // float zoom = zoomMaxOffSet - (((zoomMaxOffSet - zoomMinOffSet) / maxProgress) * progress);
        //0 Zoom: 18.0 lonOffset: 7.5E-4
        //10 Zoom: 14.0 lonOffset: 0.009
        if(progress == 0){
            progress++;
        }
        double radius = progress * 50;
        //double diameter = progress * 100;
        double scale = radius / 500;
        //float zoomLevel = (float) (16 - Math.log(scale) / Math.log(2));
        float zoomLevel = (float) (15 - Math.log(radius / 500) / Math.log(2));
        CameraPosition cameraPosition
                = new CameraPosition.Builder()
                .target(latLng)      // Sets the center of the map to Mountain View
                .zoom(zoomLevel)                   // Sets the zoom
                .tilt(30)
                .build();                   // Creates a CameraPosition from the builder
        return cameraPosition;
    }

    public static CameraPosition resetCameraPosition(){
        return Singleton.getInstance().getOldCameraPosition();
    }

}
