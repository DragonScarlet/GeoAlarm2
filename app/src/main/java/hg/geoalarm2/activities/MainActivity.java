package hg.geoalarm2.activities;


import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import hg.geoalarm2.R;
import hg.geoalarm2.fragments.AlarmDetailsListDialogFragment;
import hg.geoalarm2.fragments.DatePickerFragment;
import hg.geoalarm2.fragments.TimePickerFragment;
import hg.geoalarm2.helpers.InfoWindowAdapterHelper;
import hg.geoalarm2.managers.AnimationManager;
import hg.geoalarm2.managers.CameraManager;
import hg.geoalarm2.managers.DataManager;
import hg.geoalarm2.objects.alarm.Alarm;
import hg.geoalarm2.objects.time.Date;
import hg.geoalarm2.objects.time.Time;
import hg.geoalarm2.receivers.AlarmReceiver;
import hg.geoalarm2.services.GeofenceTransitionsIntentService;
import hg.geoalarm2.utils.MapUtils;
import hg.geoalarm2.utils.Singleton;

import static hg.geoalarm2.constants.mainActivityConstants.INTENT_MAP_KEY;
import static hg.geoalarm2.enums.State.CREATE;
import static hg.geoalarm2.enums.State.CREATE_END_DAY;
import static hg.geoalarm2.enums.State.CREATE_END_TIME;
import static hg.geoalarm2.enums.State.CREATE_START_DAY;
import static hg.geoalarm2.enums.State.CREATE_START_TIME;
import static hg.geoalarm2.enums.State.EDIT;
import static hg.geoalarm2.enums.State.NO_MARKER;
import static hg.geoalarm2.enums.State.REMOVE;
import static hg.geoalarm2.enums.State.STATUS;




public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, AlarmDetailsListDialogFragment.Listener {
    private GoogleMap mMap;
    private Context mContext;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    int seekBarOffset = 50;

    //Constants
    private final int RADIUS_STEP = 50;
    private final String DATE_FORMAT = "dd.MM.yyyy HH:mm:ss";
    private final int INITIAL_RADIUS = 500;
    private GeofencingClient mGeofencingClient;
    private ArrayList<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;

    GoogleApiClient googleApiClient = null;
    private final String TAG = "DEBUG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Singleton.getInstance().setMainActivity(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        this.mContext = this;
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        mapFragment.getMapAsync(this);
        hideDetailsMenu();
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        initAllListeners();
        Singleton.getInstance().setCurrentState(NO_MARKER);
        mGeofencingClient = LocationServices.getGeofencingClient(this);
        mGeofenceList = new ArrayList<>();

        mGeofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId("1")
                .setCircularRegion(
                        52.271649,
                        10.539307,
                        50000
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setLoiteringDelay(1000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnSuccessListener(Singleton.getInstance().getMainActivity(), new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Geofences added
                        Log.d(TAG, "Geofences successfully added!");
                    }
                })
                .addOnFailureListener(Singleton.getInstance().getMainActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to add geofences
                        Log.d(TAG, "Geofences failed to add!");
                    }
                });


        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        startLocationMonitoring();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .build();
    }


    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }


    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    private void startLocationMonitoring(){
        Log.d(TAG, "startLocation called");
        try{
            LocationRequest locationRequest = LocationRequest.create()
                    .setInterval(10000)
                    .setFastestInterval(5000)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
                    locationRequest, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            Log.d(TAG, "Location update lat/long" + location.getLatitude() + " " + location.getLongitude());
                        }
                    });
        }
        catch(SecurityException e)
        {
            Log.d(TAG, "SecurityException - " + e.getMessage());
        }
    }




    private void initAllListeners() {
        initCreateButton();
        initTimers();
        initSeekBar();
    }

    private void initCreateButton() {
        final Button createButton = (Button) findViewById(R.id.createButton);
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                1);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (areInputsFilled()) {
                    String key = addCurrentMarker();
                    resetCamera();
                    hideDetailsMenu();
                    DataManager.saveAlarms();
                    Singleton.getInstance().setCurrentState(NO_MARKER);
                    enableAlarm(key);
                } else {
                    notifyMe("Please fill all the information");
                }
            }
        });
    }

    private void enableAlarm(String key){
        Alarm alarm = Singleton.getInstance().getAlarmsMap().get(key);
        final int id = (int) System.currentTimeMillis();
        alarm.setPendingId(id);
        Singleton.getInstance().getAlarmsMap().put(key, alarm);
        Intent my_intent = new Intent(mContext, AlarmReceiver.class);
        my_intent.putExtra(INTENT_MAP_KEY, key);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, alarm.getPendingId(), my_intent, PendingIntent.FLAG_ONE_SHOT);
        try {
            Date date = Singleton.getInstance().getCacheAlarm().getStartDay();
            Time time = Singleton.getInstance().getCacheAlarm().getStartTime();
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            String strDate = date.getStrDate() + " " + time.getStrTime();
            java.util.Date parsedDate = sdf.parse(strDate);
            long millis = parsedDate.getTime();
            alarmManager.set(AlarmManager.RTC_WAKEUP, millis, pendingIntent);
            notifyMe("Alarm successfully activated!");
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void disableAlarm(String key){
        Alarm alarm = Singleton.getInstance().getAlarmsMap().get(key);
        if(pendingIntent != null && alarm.isActive()){
            Intent my_intent = new Intent(mContext, AlarmReceiver.class);
            my_intent.putExtra(INTENT_MAP_KEY, key);
            pendingIntent = PendingIntent.getBroadcast(MainActivity.this, alarm.getPendingId(), my_intent, PendingIntent.FLAG_ONE_SHOT);
            pendingIntent.cancel();
            alarmManager.cancel(pendingIntent);
            notifyMe("Alarm successfully deactivated!");
        }
    }

    private void initTimers() {
        Button btnStartTime = (Button) findViewById(R.id.button_start_time);
        btnStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Singleton.getInstance().setCurrentState(CREATE_START_TIME);
                showTimePickerDialog(view);
            }
        });

        Button btnEndTime = (Button) findViewById(R.id.button_end_time);
        btnEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Singleton.getInstance().setCurrentState(CREATE_END_TIME);
                showTimePickerDialog(view);
            }
        });

        Button btnStartDay = (Button) findViewById(R.id.button_start_day);
        btnStartDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Singleton.getInstance().setCurrentState(CREATE_START_DAY);
                showDatePickerDialog(view);
            }
        });

        Button btnEndDay = (Button) findViewById(R.id.button_end_day);
        btnEndDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Singleton.getInstance().setCurrentState(CREATE_END_DAY);
                showDatePickerDialog(view);
            }
        });
    }

    private void initSeekBar() {
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar_alarm_area);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                paintCircle((progress + 1) * seekBarOffset);
                zoomWithStyle(Singleton.getInstance().getCurrentMarker().getPosition(), progress);
            }
        });
    }

    private void hideDetailsMenu() {
        AnimationManager.hideDetailsMenu(this, mMap);
        removeCircle();
    }

    private void showDetailsMenu(Marker marker) {
        Alarm alarm = Singleton.getInstance().getAlarmsMap().get(MapUtils.getMapKey(marker));
        setAlarmDetails(alarm);
        AnimationManager.showDetailsMenu(this, mMap);
        if (alarm != null) {
            moveWithStyleToPosition(Singleton.getInstance().getCurrentMarker().getPosition(), alarm.getRadius());
        } else {
            moveWithStyleToPosition(Singleton.getInstance().getCurrentMarker().getPosition(), INITIAL_RADIUS);
        }
    }


    private void setAlarmDetails(Alarm alarm) {
        EditText alarmName = (EditText) findViewById(R.id.edit_alarm_name);
        /*
        CheckBox monday = (CheckBox) findViewById(R.id.checkbox_monday);
        CheckBox tuesday = (CheckBox) findViewById(R.id.checkbox_tuesday);
        CheckBox wednesday = (CheckBox) findViewById(R.id.checkbox_wednesday);
        CheckBox thursday = (CheckBox) findViewById(R.id.checkbox_thursday);
        CheckBox friday = (CheckBox) findViewById(R.id.checkbox_friday);
        CheckBox saturday = (CheckBox) findViewById(R.id.checkbox_saturday);
        CheckBox sunday = (CheckBox) findViewById(R.id.checkbox_sunday);
        */
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar_alarm_area);
        Button btnStartTime = (Button) findViewById(R.id.button_start_time);
        Button btnEndTime = (Button) findViewById(R.id.button_end_time);
        Button btnStartDay = (Button) findViewById(R.id.button_start_day);
        Button btnEndDay = (Button) findViewById(R.id.button_end_day);
        if (alarm != null) {
            alarmName.setText(alarm.getName());
            int progress = alarm.getRadius() / RADIUS_STEP;
            seekBar.setProgress(progress);
            /*
            monday.setChecked(alarm.getWeek().isMonday());
            tuesday.setChecked(alarm.getWeek().isTuesday());
            wednesday.setChecked(alarm.getWeek().isWednesday());
            thursday.setChecked(alarm.getWeek().isThursday());
            friday.setChecked(alarm.getWeek().isFriday());
            saturday.setChecked(alarm.getWeek().isSaturday());
            sunday.setChecked(alarm.getWeek().isSunday());
            */
            if (alarm.getStartTime() != null) {
                btnStartTime.setText(alarm.getStartTime().getStrTime());
                Singleton.getInstance().getCacheAlarm().setStartTime(alarm.getStartTime());
            }
            if (alarm.getEndTime() != null) {
                btnEndTime.setText(alarm.getEndTime().getStrTime());
                Singleton.getInstance().getCacheAlarm().setEndTime(alarm.getEndTime());
            }
            if (alarm.getStartDay() != null) {
                btnStartDay.setText(alarm.getStartDay().getStrDate());
                Singleton.getInstance().getCacheAlarm().setStartDay(alarm.getStartDay());
            }
            if (alarm.getEndDay() != null) {
                btnEndDay.setText(alarm.getEndDay().getStrDate());
                Singleton.getInstance().getCacheAlarm().setEndDay(alarm.getEndDay());
            }
            paintCircle(alarm.getRadius());
        } else {
            alarmName.setText("New Alarm");
            int progress = INITIAL_RADIUS / RADIUS_STEP;
            seekBar.setProgress(progress);
            /*
            monday.setChecked(false);
            tuesday.setChecked(false);
            wednesday.setChecked(false);
            thursday.setChecked(false);
            friday.setChecked(false);
            saturday.setChecked(false);
            sunday.setChecked(false);
            */
            btnStartTime.setText("set time");
            btnEndTime.setText("set time");
            btnStartDay.setText("set time");
            btnEndDay.setText("set time");
            paintCircle(RADIUS_STEP);
        }
    }

    private boolean areInputsFilled() {
        Alarm alarm = Singleton.getInstance().getCacheAlarm();
        EditText alarmName = (EditText) findViewById(R.id.edit_alarm_name);
        if (alarm.getEndDay() != null && alarm.getStartDay() != null && alarm.getStartTime() != null && alarm.getEndTime() != null && !alarmName.getText().toString().trim().equals("")) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        HashMap<String, Alarm> alarms = DataManager.getAlarms();
        Singleton.getInstance().setAlarmsMap(alarms);
        initMap(googleMap);

    }

    private void initMap(GoogleMap googleMap){
        initGoogleMaps(googleMap);
        initAllAlarmOnMap();
        initInfoWindowAdapter();
        initAllMapListeners();
    }

    private void initGoogleMaps(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    private void initAllAlarmOnMap() {
        for (String strLatLng : Singleton.getInstance().getAlarmsMap().keySet()) {
            Alarm alarm = Singleton.getInstance().getAlarmsMap().get(strLatLng);
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.ic_res);
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(alarm.getLatLng())
                    .title(alarm.getName())
                    .icon(icon);
            mMap.addMarker(markerOptions);
        }

    }

    private void initAllMapListeners() {
        initOnMapClickListener();
        initOnMarkerClickListener();
        initInfoWindowListener();
    }

    private void initOnMapClickListener() {
        mMap.setOnMapClickListener(new OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                hideDetailsMenu();
                handleNewMarkers(point);
            }
        });

    }

    private void initOnMarkerClickListener() {
        mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                hideDetailsMenu();
                if (marker != null && Singleton.getInstance().getAlarmsMap().containsKey(MapUtils.getMapKey(marker))) {
                    removeTempMarker();
                    if (Singleton.getInstance().getCurrentState().equals(STATUS)) {
                        Alarm alarm = Singleton.getInstance().getAlarmsMap().get(MapUtils.getMapKey(marker));
                        marker.setTitle( alarm.getName()) ;
                        Singleton.getInstance().setCurrentState(EDIT);
                    }
                    else if(Singleton.getInstance().getCurrentState().equals(EDIT)){
                        Singleton.getInstance().setCurrentState(REMOVE);
                    }
                    else{
                        Singleton.getInstance().setCurrentState(STATUS);
                    }
                } else {
                    marker.setTitle("Create");
                    Singleton.getInstance().setCurrentState(CREATE);
                }
                Singleton.getInstance().setCurrentMarker(marker);
                notifyMe(Singleton.getInstance().getCurrentState().toString());
                return false;
            }
        });
    }

    private void initInfoWindowAdapter(){
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                InfoWindowAdapterHelper infoHelper = new InfoWindowAdapterHelper(mContext);
                return infoHelper.getInfoTextLayout(marker);
            }
        });
    }


    private void initInfoWindowListener() {
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                resetCamera();
                switch (Singleton.getInstance().getCurrentState()) {
                    case CREATE:
                        Singleton.getInstance().setCacheAlarm(new Alarm());
                        Singleton.getInstance().setOldCameraPosition(mMap.getCameraPosition());
                        showDetailsMenu(marker);
                        break;
                    case REMOVE:
                        removeCurrentMarker();
                        break;
                    case EDIT:
                        Singleton.getInstance().setOldCameraPosition(mMap.getCameraPosition());
                        removeTempMarker();
                        showDetailsMenu(marker);
                        break;
                    case STATUS:
                        changeStatus(marker);
                        notifyMe("Status set to: " + Singleton.getInstance().getCurrentAlarm().isActive());
                    default:
                        break;
                }
                marker.hideInfoWindow();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    public void onAlarmDetailsClicked(int position) {

    }

    private void moveWithStyleToPosition(LatLng latLng, int radius) {
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraManager.getCameraPositionWithStyle(latLng, radius)));
    }

    private void zoomWithStyle(LatLng latLng, int progress) {
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraManager.getCameraPositionWhileZooming(latLng, progress)));
    }

    private void resetCamera() {
        if (Singleton.getInstance().getOldCameraPosition() != null) {
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraManager.resetCameraPosition()));
        }
    }

    private void moveToPosition(LatLng latLng) {
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraManager.getCameraPosition(latLng, mMap.getCameraPosition().zoom)));
    }

    private void handleNewMarkers(LatLng point) {
        Singleton.getInstance().setCurrentState(NO_MARKER);
        removeTempMarker();
        removeCircle();
        createMarker(point);
        moveToPosition(point);
        notifyMe(Singleton.getInstance().getCurrentState().toString());
    }

    private void removeTempMarker() {
        if (Singleton.getInstance().getCurrentMarker() != null &&
                !Singleton.getInstance().getAlarmsMap().containsKey(MapUtils.getMapKey(Singleton.getInstance().getCurrentMarker()))) {
            removeCurrentMarker();
        }
    }

    private void createMarker(LatLng point) {
        Marker tmp = mMap.addMarker(new MarkerOptions().position(point).title("Create New Alarm"));
        Singleton.getInstance().setCurrentMarker(tmp);
    }

    private void removeCurrentMarker() {
        if (Singleton.getInstance().getCurrentMarker() != null) {
            Singleton.getInstance().getAlarmsMap().remove(MapUtils.getMapKey(Singleton.getInstance().getCurrentMarker()));
            Singleton.getInstance().getCurrentMarker().remove();
            Singleton.getInstance().setCurrentMarker(null);
            DataManager.saveAlarms();
        }
        Singleton.getInstance().setCurrentState(NO_MARKER);
        resetCamera();
    }

    private void changeStatus(Marker marker){
        Alarm alarm = Singleton.getInstance().getAlarmsMap().get(MapUtils.getMapKey(marker));
        String key = MapUtils.getMapKey(marker);
        if(alarm.isActive()){
            disableAlarm(key);
        }
        else{
            enableAlarm(key);
        }
        alarm.toogleAlarm();
        Singleton.getInstance().getAlarmsMap().put(MapUtils.getMapKey(marker), alarm);
        DataManager.saveAlarms();
    }

    private String addCurrentMarker() {
        String key = "";
        if (Singleton.getInstance().getCurrentMarker() != null) {
            EditText alarmName = (EditText) findViewById(R.id.edit_alarm_name);
            SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar_alarm_area);
            int radius = seekBar.getProgress() * 50;
            Singleton.getInstance().getCurrentAlarm().setRadius(radius);
            Singleton.getInstance().getCurrentAlarm().setLatitude(Singleton.getInstance().getCurrentMarker().getPosition().latitude);
            Singleton.getInstance().getCurrentAlarm().setLongitude(Singleton.getInstance().getCurrentMarker().getPosition().longitude);
            Alarm.type type = Alarm.type.SINGLE;
            Alarm alarm = new Alarm(
                    alarmName.getText().toString(),
                    Singleton.getInstance().getCacheAlarm().getStartTime(),
                    Singleton.getInstance().getCacheAlarm().getEndTime(),
                    Singleton.getInstance().getCacheAlarm().getStartDay(),
                    Singleton.getInstance().getCacheAlarm().getEndDay(),
                    radius,
                    true,
                    type,
                    null,
                    Singleton.getInstance().getCurrentMarker().getPosition().latitude,
                    Singleton.getInstance().getCurrentMarker().getPosition().longitude);
            key = MapUtils.getMapKey(Singleton.getInstance().getCurrentMarker());
            Singleton.getInstance().getAlarmsMap().put(key, alarm);
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.ic_res);
            Singleton.getInstance().getCurrentMarker().setIcon(icon);
        }
        return key;
    }
/*
    private Week getWeek() {
        CheckBox monday = (CheckBox) findViewById(R.id.checkbox_monday);
        CheckBox tuesday = (CheckBox) findViewById(R.id.checkbox_tuesday);
        CheckBox wednesday = (CheckBox) findViewById(R.id.checkbox_wednesday);
        CheckBox thursday = (CheckBox) findViewById(R.id.checkbox_thursday);
        CheckBox friday = (CheckBox) findViewById(R.id.checkbox_friday);
        CheckBox saturday = (CheckBox) findViewById(R.id.checkbox_saturday);
        CheckBox sunday = (CheckBox) findViewById(R.id.checkbox_sunday);
        return new Week(monday.isChecked(),
                tuesday.isChecked(),
                wednesday.isChecked(),
                thursday.isChecked(),
                friday.isChecked(),
                saturday.isChecked(),
                sunday.isChecked());
    }
    */

    private void notifyMe(String msg) {
        Context context = getApplicationContext();
        CharSequence text = msg;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    /*
        public void onCheckboxClicked(View view) {
            switch (view.getId()) {
                case R.id.checkbox_monday:
                    break;
                case R.id.checkbox_tuesday:
                    break;
            }
        }
        public void onSwitchClicked(View view) {
            View weekdays = findViewById(R.id.weekdays);
            Switch switchView = (Switch) findViewById(R.id.switch_multiple_times);
            if (switchView.isChecked()) {
                weekdays.setVisibility(View.VISIBLE);
            } else {
                weekdays.setVisibility(View.GONE);
            }
        }
    */
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }


    private void paintCircle(int radius) {
        removeCircle();
        Singleton.getInstance().setCircle(mMap.addCircle(new CircleOptions()
                .center(Singleton.getInstance().getCurrentMarker().getPosition())
                .radius(radius)
                .strokeColor(0x220000FF)
                .fillColor(0x220000FF)));
    }

    private void removeCircle() {
        if (Singleton.getInstance().getCircle() != null) {
            Singleton.getInstance().getCircle().remove();
        }
    }


}
