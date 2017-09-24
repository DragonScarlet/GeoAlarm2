package hg.geoalarm2.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.Toast;

import hg.geoalarm2.R;

/**
 * Created by dimkn on 9/7/2017.
 */

public class RingtonePlayingService extends Service {

    MediaPlayer media_song;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        media_song = MediaPlayer.create(this, R.raw.persona5);
        media_song.start();

//        Geofence geofence = new Geofence.Builder()
//                .setRequestId(startId)
//                .setCircularRegion(
//                        entry.getValue().latitude,
//                        entry.getValue().longitude,
//                        Constants.GEOFENCE_RADIUS_IN_METERS
//                )
//                .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
//                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
//                        Geofence.GEOFENCE_TRANSITION_EXIT)
//                .build();




        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }
}
