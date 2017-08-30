package hg.geoalarm2;


import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.touchboarder.weekdaysbuttons.WeekdaysDataSource;

import java.util.HashMap;

import static hg.geoalarm2.State.CREATE;
import static hg.geoalarm2.State.CREATE_END_DAY;
import static hg.geoalarm2.State.CREATE_END_TIME;
import static hg.geoalarm2.State.CREATE_START_DAY;
import static hg.geoalarm2.State.CREATE_START_TIME;
import static hg.geoalarm2.State.EDIT;
import static hg.geoalarm2.State.NO_MARKER;
import static hg.geoalarm2.State.REMOVE;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, AlarmDetailsListDialogFragment.Listener {

    boolean hide = true;
    private GoogleMap mMap;
    public Log log;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Singleton.getInstance().setMainActivity(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        hideDetailsMenu();
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        final FloatingActionButton fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        final FloatingActionButton deleteButton = (FloatingActionButton) findViewById(R.id.deleteButton);
        final Button createButton = (Button) findViewById(R.id.createButton);
        final FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.addButton);
        final Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                fab.startAnimation(shake);
                AlarmDetailsListDialogFragment.newInstance(5).show(getSupportFragmentManager(), "dialog");
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteButton.startAnimation(shake);
                removeCurrentMarker();
            }
        } );

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addButton.startAnimation(shake);
                addCurrentMarker();
            }
        } );

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCurrentMarker();
                resetCamera();
                hideDetailsMenu();
                DataManager.saveHashMap("Alarms", Singleton.getInstance().getAlarmsMap());
                Singleton.getInstance().setCurrentState(NO_MARKER);
            }
        } );

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

        RadioButton radio1 = (RadioButton) findViewById(R.id.radio_single);
        radio1.setChecked(true);

        Spinner spinner = (Spinner) findViewById(R.id.planets_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        Singleton.getInstance().setCurrentState(NO_MARKER);
    }

    private void createAlarm(){
        hideDetailsMenu();
    }

    private void hideDetailsMenu(){
        AnimationManager.hideDetailsMenu(this);
    }

    private void showDetailsMenu(){
        AnimationManager.showDetailsMenu(this);
    }

    private void animateBtn(FloatingActionButton btn, int x, int y, int time){
        btn.animate()
                .translationX(x)
                .translationY(y)
                .setDuration(time)
                .start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap){
        HashMap<String, Alarm> alarms = DataManager.getHashMap("Alarms");

        Singleton.getInstance().setAlarmsMap(alarms);

        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);

        for (String strLatLng : Singleton.getInstance().getAlarmsMap().keySet()) {
            Alarm alarm = Singleton.getInstance().getAlarmsMap().get(strLatLng);
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.ic_res);
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(alarm.getLatLng())
                    .title("Edit")
                    .icon(icon);
            mMap.addMarker(markerOptions);
        }

        mMap.setOnMapClickListener(new OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                hideDetailsMenu();
                handleNewMarkers(point);
            }
        });

        mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                hideDetailsMenu();
                if(Singleton.getInstance().getAlarmsMap().containsKey(getMapKey(Singleton.getInstance().getCurrentMarker()))){
                    removeTempMarker();
                    if(!marker.getTitle().equals("Edit")){
                        marker.setTitle("Edit");
                        Singleton.getInstance().setCurrentState(EDIT);
                    }
                    else{
                        marker.setTitle("Remove");
                        Singleton.getInstance().setCurrentState(REMOVE);
                    }
                }
                else{
                    marker.setTitle("Create");
                    Singleton.getInstance().setCurrentState(CREATE);
                }

                Singleton.getInstance().setCurrentMarker(marker);
                notifyMe(Singleton.getInstance().getCurrentState().toString());

                return false;


            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                switch (Singleton.getInstance().getCurrentState()){
                    case CREATE:
                        Singleton.getInstance().setOldCameraPosition(mMap.getCameraPosition());
                        showDetailsMenu();
                        moveWithStyleToPosition(Singleton.getInstance().getCurrentMarker().getPosition());
                        break;
                    case REMOVE:
                        removeCurrentMarker();
                        break;
                    case EDIT:
                        Singleton.getInstance().setOldCameraPosition(mMap.getCameraPosition());
                        removeTempMarker();
                        showDetailsMenu();
                        moveWithStyleToPosition(Singleton.getInstance().getCurrentMarker().getPosition());
                        break;
                    default:
                        break;
                }
                marker.hideInfoWindow();
            }
        });
    }

    @Override
    public void onAlarmDetailsClicked(int position) {

    }

    private void moveWithStyleToPosition(LatLng latLng){
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraManager.getCameraPositionWithStyle(latLng)));
    }

    private void resetCamera(){
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraManager.resetCameraPosition()));
    }

    private void moveToPosition(LatLng latLng){
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraManager.getCameraPosition(latLng, mMap.getCameraPosition().zoom)));
    }

    private void handleNewMarkers(LatLng point){
        Singleton.getInstance().setCurrentState(NO_MARKER);
        handelNewMarker(point);
        moveToPosition(point);
        notifyMe(Singleton.getInstance().getCurrentState().toString());
    }

    private void handelNewMarker(LatLng point){
        removeTempMarker();
//        deleteCurrentMarker();
        createMarker(point);
    }

    private void removeTempMarker(){
        if(Singleton.getInstance().getCurrentMarker() != null &&
                !Singleton.getInstance().getAlarmsMap().containsKey(getMapKey(Singleton.getInstance().getCurrentMarker()))){
            removeCurrentMarker();
        }
    }

    private void handelNoMarker(LatLng point){
        createMarker(point);
    }

    private void createMarker(LatLng point){
        Marker tmp =  mMap.addMarker(new MarkerOptions().position(point).title("Create New Alarm"));
        Singleton.getInstance().setCurrentMarker(tmp);
        //FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.addButton);
//        animateBtn(addButton, 0, 100, 250);
    }

    private void removeCurrentMarker(){
        if(Singleton.getInstance().getCurrentMarker() != null){
            Singleton.getInstance().getCurrentMarker().remove();
            Singleton.getInstance().setCurrentMarker(null);
        }
        Singleton.getInstance().setCurrentState(NO_MARKER);
    }

    private void addCurrentMarker(){
        if(Singleton.getInstance().getCurrentMarker() != null){
            EditText alarmName = (EditText) findViewById(R.id.edit_alarm_name);









            Alarm tmp = new Alarm(alarmName.getText().toString(),null,null,null,null, 0, false, null, null,
                    Singleton.getInstance().getCurrentMarker().getPosition().latitude, Singleton.getInstance().getCurrentMarker().getPosition().longitude);
            Singleton.getInstance().getAlarmsMap().put(getMapKey(Singleton.getInstance().getCurrentMarker()), tmp);
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.ic_res);
            Singleton.getInstance().getCurrentMarker().setIcon(icon);
        }
    }

    private void notifyMe(String msg){
        Context context = getApplicationContext();
        CharSequence text = msg;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_single:
                if (checked){
                    notifyMe("Single");
                }
                    // Pirates are the best
                    break;
            case R.id.radio_multiple:
                if (checked){
                    notifyMe("Multiple");
                }
                    break;
        }
    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkbox_monday:
                break;
            case R.id.checkbox_tuesday:
                break;
        }
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(),"timePicker");
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private String getMapKey(Marker marker){
        return marker.getPosition().latitude + "###" + marker.getPosition().longitude;
    }

}
