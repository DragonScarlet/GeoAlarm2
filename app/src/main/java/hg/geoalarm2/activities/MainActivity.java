package hg.geoalarm2.activities;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
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
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
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

import java.text.ParseException;
import java.util.HashMap;

import hg.geoalarm2.objects.alarm.Alarm;
import hg.geoalarm2.fragments.AlarmDetailsListDialogFragment;
import hg.geoalarm2.receivers.AlarmReceiver;
import hg.geoalarm2.managers.DataManager;
import hg.geoalarm2.objects.time.Date;
import hg.geoalarm2.fragments.DatePickerFragment;
import hg.geoalarm2.R;
import hg.geoalarm2.utils.Singleton;
import hg.geoalarm2.objects.time.Time;
import hg.geoalarm2.fragments.TimePickerFragment;
import hg.geoalarm2.objects.time.Week;
import hg.geoalarm2.managers.AnimationManager;
import hg.geoalarm2.managers.CameraManager;

import static hg.geoalarm2.enums.State.CREATE;
import static hg.geoalarm2.enums.State.CREATE_END_DAY;
import static hg.geoalarm2.enums.State.CREATE_END_TIME;
import static hg.geoalarm2.enums.State.CREATE_START_DAY;
import static hg.geoalarm2.enums.State.CREATE_START_TIME;
import static hg.geoalarm2.enums.State.EDIT;
import static hg.geoalarm2.enums.State.NO_MARKER;
import static hg.geoalarm2.enums.State.REMOVE;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, AlarmDetailsListDialogFragment.Listener {

    boolean hide = true;
    private GoogleMap mMap;
    public Log log;
    Context context;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Singleton.getInstance().setMainActivity(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        this.context = this;

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        mapFragment.getMapAsync(this);
        hideDetailsMenu();
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        final FloatingActionButton fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        final FloatingActionButton deleteButton = (FloatingActionButton) findViewById(R.id.deleteButton);
        final Button createButton = (Button) findViewById(R.id.createButton);
        final FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.addButton);
        final Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);

        // TEST
        // Create an intent to the alarm receiver class



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
                if(areInputsFilled()){
                    addCurrentMarker();
                    resetCamera();
                    hideDetailsMenu();
                    DataManager.saveAlarms();
                    Singleton.getInstance().setCurrentState(NO_MARKER);
                    Intent my_intent = new Intent(context, AlarmReceiver.class);
                    final int _id = (int) System.currentTimeMillis();
                    pendingIntent = PendingIntent.getBroadcast(MainActivity.this, _id, my_intent, PendingIntent.FLAG_ONE_SHOT);
                    try {
                        Date date = Singleton.getInstance().getCacheAlarm().getStartDay();
                        Time time = Singleton.getInstance().getCacheAlarm().getStartTime();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                        String strDate = date. getStrDate() + " " + time.getStrTime();
                        java.util.Date parsedDate = sdf.parse(strDate);
                        long millis = parsedDate.getTime();
                        alarmManager.set(AlarmManager.RTC_WAKEUP, millis , pendingIntent );
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
                else{
                    notifyMe("Please fill all the information");
                }
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

    private void showDetailsMenu(Marker marker){
        Alarm alarm = Singleton.getInstance().getAlarmsMap().get(getMapKey(marker));
        setAlarmDetails(alarm);
        AnimationManager.showDetailsMenu(this);
    }

    private void setAlarmDetails(Alarm alarm){
        if(alarm != null){
            EditText alarmName = (EditText) findViewById(R.id.edit_alarm_name);
            alarmName.setText(alarm.getName());

            SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar_alarm_area);
            seekBar.setProgress(alarm.getRadius());

            CheckBox monday = (CheckBox) findViewById(R.id.checkbox_monday);
            monday.setChecked(alarm.getWeek().isMonday());
            CheckBox tuesday = (CheckBox) findViewById(R.id.checkbox_tuesday);
            tuesday.setChecked(alarm.getWeek().isTuesday());
            CheckBox wednesday = (CheckBox) findViewById(R.id.checkbox_wednesday);
            wednesday.setChecked(alarm.getWeek().isWednesday());
            CheckBox thursday = (CheckBox) findViewById(R.id.checkbox_thursday);
            thursday.setChecked(alarm.getWeek().isThursday());
            CheckBox friday = (CheckBox) findViewById(R.id.checkbox_friday);
            friday.setChecked(alarm.getWeek().isFriday());
            CheckBox saturday = (CheckBox) findViewById(R.id.checkbox_saturday);
            saturday.setChecked(alarm.getWeek().isSaturday());
            CheckBox sunday = (CheckBox) findViewById(R.id.checkbox_sunday);
            sunday.setChecked(alarm.getWeek().isSunday());

//        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_group_selection);
            RadioButton radioButton = (RadioButton) findViewById(R.id.radio_single);
            if(alarm.getType().equals(Alarm.type.MULTIPLE)){
                radioButton = (RadioButton) findViewById(R.id.radio_multiple);
            }
            radioButton.setChecked(true);

            Button btnStartTime = (Button) findViewById(R.id.button_start_time);
            if(alarm.getStartTime() != null){
                btnStartTime.setText(alarm.getStartTime().getStrTime());
                Singleton.getInstance().getCacheAlarm().setStartTime(alarm.getStartTime());
            }
            Button btnEndTime = (Button) findViewById(R.id.button_end_time);
            if(alarm.getEndTime() != null){
                btnEndTime.setText(alarm.getEndTime().getStrTime());
                Singleton.getInstance().getCacheAlarm().setEndTime(alarm.getEndTime());
            }
            Button btnStartDay = (Button) findViewById(R.id.button_start_day);
            if(alarm.getStartDay() != null){
                btnStartDay.setText(alarm.getStartDay().getStrDate());
                Singleton.getInstance().getCacheAlarm().setStartDay(alarm.getStartDay());
            }
            Button btnEndDay = (Button) findViewById(R.id.button_end_day);
            if(alarm.getEndDay() != null){
                btnEndDay.setText(alarm.getEndDay().getStrDate());
                Singleton.getInstance().getCacheAlarm().setEndDay(alarm.getEndDay());
            }
        }
        else{
            EditText alarmName = (EditText) findViewById(R.id.edit_alarm_name);
            alarmName.setText("");
            SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar_alarm_area);
            seekBar.setProgress(0);

            CheckBox monday = (CheckBox) findViewById(R.id.checkbox_monday);
            monday.setChecked(false);
            CheckBox tuesday = (CheckBox) findViewById(R.id.checkbox_tuesday);
            tuesday.setChecked(false);
            CheckBox wednesday = (CheckBox) findViewById(R.id.checkbox_wednesday);
            wednesday.setChecked(false);
            CheckBox thursday = (CheckBox) findViewById(R.id.checkbox_thursday);
            thursday.setChecked(false);
            CheckBox friday = (CheckBox) findViewById(R.id.checkbox_friday);
            friday.setChecked(false);
            CheckBox saturday = (CheckBox) findViewById(R.id.checkbox_saturday);
            saturday.setChecked(false);
            CheckBox sunday = (CheckBox) findViewById(R.id.checkbox_sunday);
            sunday.setChecked(false);
            RadioButton radioButton = (RadioButton) findViewById(R.id.radio_single);
            radioButton.setChecked(true);

            Button btnStartTime = (Button) findViewById(R.id.button_start_time);
            btnStartTime.setText("set time");
            Button btnEndTime = (Button) findViewById(R.id.button_end_time);
            btnEndTime.setText("set time");
            Button btnStartDay = (Button) findViewById(R.id.button_start_day);
            btnStartDay.setText("set time");
            Button btnEndDay = (Button) findViewById(R.id.button_end_day);
            btnEndDay.setText("set time");
        }

    }


    private void animateBtn(FloatingActionButton btn, int x, int y, int time){
        btn.animate()
                .translationX(x)
                .translationY(y)
                .setDuration(time)
                .start();
    }

    private boolean areInputsFilled(){
        Alarm alarm = Singleton.getInstance().getCacheAlarm();
        EditText alarmName = (EditText) findViewById(R.id.edit_alarm_name);
        if(alarm.getEndDay() != null && alarm.getStartDay() != null && alarm.getStartTime() != null  && alarm.getEndTime() != null && !alarmName.getText().toString().trim().equals("") ){
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
        HashMap<String, Alarm> alarms = DataManager.getAlarms();

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
                if(marker != null && Singleton.getInstance().getAlarmsMap().containsKey(getMapKey(marker))){
                    removeTempMarker();
                    if(marker.getTitle().equals("Remove")){
                        Alarm alarm = Singleton.getInstance().getAlarmsMap().get(getMapKey(marker));
                        marker.setTitle(alarm.getName());
                        Singleton.getInstance().setCurrentState(EDIT);
                    }
                    else{
                        marker.setTitle("Remove");
                        Singleton.getInstance().setCurrentState(REMOVE);
                    }
                }
                else{

                    marker.setTitle("Create");
//                    if(Singleton.getInstance().getCurrentState().equals(CREATE) || Singleton.getInstance().getCurrentState().equals(EDIT) || Singleton.getInstance().getCurrentState().equals(NO_MARKER)){
//                        resetCamera();
//                    }
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
                        Singleton.getInstance().setCacheAlarm(new Alarm());
                        Singleton.getInstance().setOldCameraPosition(mMap.getCameraPosition());
                        showDetailsMenu(marker);
                        moveWithStyleToPosition(Singleton.getInstance().getCurrentMarker().getPosition());
                        break;
                    case REMOVE:
                        removeCurrentMarker();
                        break;
                    case EDIT:
                        Singleton.getInstance().setOldCameraPosition(mMap.getCameraPosition());
                        removeTempMarker();
                        showDetailsMenu(marker);
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
            Singleton.getInstance().getAlarmsMap().remove(getMapKey(Singleton.getInstance().getCurrentMarker()));
            Singleton.getInstance().getCurrentMarker().remove();
            Singleton.getInstance().setCurrentMarker(null);
            DataManager.saveAlarms();
        }
        Singleton.getInstance().setCurrentState(NO_MARKER);
    }

    private void addCurrentMarker(){
        if(Singleton.getInstance().getCurrentMarker() != null){
            EditText alarmName = (EditText) findViewById(R.id.edit_alarm_name);
            SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar_alarm_area);
            Singleton.getInstance().getCurrentAlarm().setRadius(seekBar.getProgress());
            Singleton.getInstance().getCurrentAlarm().setLatitude(Singleton.getInstance().getCurrentMarker().getPosition().latitude);
            Singleton.getInstance().getCurrentAlarm().setLongitude(Singleton.getInstance().getCurrentMarker().getPosition().longitude);

            RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_group_selection);
            int selectedId = radioGroup.getCheckedRadioButtonId();
            RadioButton radioButton = (RadioButton) findViewById(selectedId);

            Alarm.type type = Alarm.type.SINGLE;
            if(radioButton.getText().equals("Multiple")){
                type = Alarm.type.MULTIPLE;
            }

            Alarm alarm = new Alarm(
                    alarmName.getText().toString(),
                    Singleton.getInstance().getCacheAlarm().getStartTime(),
                    Singleton.getInstance().getCacheAlarm().getEndTime(),
                    Singleton.getInstance().getCacheAlarm().getStartDay(),
                    Singleton.getInstance().getCacheAlarm().getEndDay(),
                    seekBar.getProgress(),
                    true,
                    type,
                    getWeek(),
                    Singleton.getInstance().getCurrentMarker().getPosition().latitude,
                    Singleton.getInstance().getCurrentMarker().getPosition().longitude);
            Singleton.getInstance().getAlarmsMap().put(getMapKey(Singleton.getInstance().getCurrentMarker()), alarm);
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.ic_res);
            Singleton.getInstance().getCurrentMarker().setIcon(icon);
        }
    }

    private Week getWeek(){
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
        if(marker !=  null){
            return marker.getPosition().latitude + "###" + marker.getPosition().longitude;
        }
        else{
            return "";
        }
    }

}
