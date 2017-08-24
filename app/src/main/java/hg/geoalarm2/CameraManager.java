package hg.geoalarm2;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

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

    public static CameraPosition getCameraPositionWithStyle(LatLng latLng){
        CameraPosition cameraPosition
                = new CameraPosition.Builder()
                .target(latLng)      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        return cameraPosition;
    }

    public static CameraPosition resetCameraPosition(){
        return Singleton.getInstance().getOldCameraPosition();
    }
}
