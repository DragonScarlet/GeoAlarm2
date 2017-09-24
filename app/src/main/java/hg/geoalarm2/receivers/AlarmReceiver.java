package hg.geoalarm2.receivers;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import hg.geoalarm2.objects.alarm.Alarm;
import hg.geoalarm2.services.GeofenceTransitionsIntentService;
import hg.geoalarm2.utils.DateUtils;
import hg.geoalarm2.utils.Singleton;

import static hg.geoalarm2.constants.mainActivityConstants.INTENT_MAP_KEY;

/**
 * Created by dimkn on 9/5/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {

    Context mContext;
    final int DELAY = 5000;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("DEBUG", "I got you in my sight!");
        mContext = context;
//        Intent service_intent = new Intent(context, RingtonePlayingService.class);

        String mapKey = intent.getStringExtra(INTENT_MAP_KEY);
        Alarm alarm = Singleton.getInstance().getAlarmsMap().get(mapKey);

//        Intent popUpIntent = new Intent(context, AlarmActivity.class);

//        context.startActivity(popUpIntent);
        //context.startService(service_intent);
        Geofence geofence = new Geofence.Builder()
                .setRequestId(mapKey)
                .setCircularRegion(
                        alarm.getLatitude(),
                        alarm.getLongitude(),
                        alarm.getRadius()
                )
                .setLoiteringDelay(DELAY)
                .setExpirationDuration(DateUtils.getTimeDifference(alarm))
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL)
                .build();
        Singleton.getInstance().getGeofenceMap().put(mapKey, geofence);

        addGeofences();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (Singleton.getInstance().getGeofencePendingIntent() != null) {
            return Singleton.getInstance().getGeofencePendingIntent();
        }
        Intent intent = new Intent(Singleton.getInstance().getMainActivity(), GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        Singleton.getInstance().setGeofencePendingIntent(PendingIntent.getService(Singleton.getInstance().getMainActivity(), 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT));
        return Singleton.getInstance().getGeofencePendingIntent();
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        List geofences = new ArrayList(Singleton.getInstance().getGeofenceMap().values());
        builder.addGeofences(geofences);
        return builder.build();
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(Singleton.getInstance().getMainActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Adds geofences. This method should be called after the user has granted the location
     * permission.
     */
    @SuppressWarnings("MissingPermission")
    private void addGeofences() {
        if (!checkPermissions()) {
            showSnackbar("SSSS");
            return;
        }

        Singleton.getInstance().getGeofencingClient().addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnSuccessListener(Singleton.getInstance().getMainActivity(), new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Geofences added
                        Log.d("DEBUG", "Geofences successfully added!");
                    }
                })
                .addOnFailureListener(Singleton.getInstance().getMainActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to add geofences
                        Log.d("DEBUG", "Geofences failed to add!");
                    }
                });
    }

    /**
     * Shows a {@link Snackbar} using {@code text}.
     *
     * @param text The Snackbar text.
     */
    private void showSnackbar(final String text) {
        View container = Singleton.getInstance().getMainActivity().findViewById(android.R.id.content);
        if (container != null) {
            Snackbar.make(container, text, 5000).show();
        }
    }



}
