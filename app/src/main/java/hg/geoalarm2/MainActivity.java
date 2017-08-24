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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
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

import static hg.geoalarm2.MainActivity.status.CREATE;
import static hg.geoalarm2.MainActivity.status.EDIT;
import static hg.geoalarm2.MainActivity.status.NO_MARKER;
import static hg.geoalarm2.MainActivity.status.REMOVE;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, AlarmDetailsListDialogFragment.Listener {

    boolean hide = true;
    private GoogleMap mMap;
    private HashMap<LatLng, Alarm> alarmsMap;
    public Log log;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
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
               /*
                int offset = -300;
                int tweaker = 0;
                int animationTime = 1000;
                if(hide){
                    hide = false;
                    fab2.setEnabled(true);
                    fab3.setEnabled(true);
                    deleteButton.setEnabled(true);
                    animateBtn(fab2, 0, (offset * 1) - tweaker, 0, 0, animationTime);
                    animateBtn(fab3, 0, (offset * 2) - tweaker,0,0, animationTime);
                    animateBtn(deleteButton, 0, (offset * 3) - tweaker,0,0, animationTime);
                    deleteButton.setTranslationX((offset * 3) - tweaker);
                }
                else{
                    hide = true;
                    fab2.setEnabled(false);
                    fab3.setEnabled(false);
                    deleteButton.setEnabled(false);
                    animateBtn(fab2, (offset * 1) - tweaker, 0, 0, 0, animationTime);
                    animateBtn(fab3, (offset * 2) - tweaker, 0, 0,0, animationTime);
                    animateBtn(deleteButton, (offset * 3) - tweaker, 0, 0,0, animationTime);
                }
*/
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
                currentStatus = NO_MARKER;
            }
        } );

        RadioButton radio1 = (RadioButton) findViewById(R.id.radio_single);
        radio1.setChecked(true);
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
//        Animation animation = new TranslateAnimation(x1,x2,y1,y2);
//        animation.setDuration(time);
//        animation.setFillAfter(true);
//        btn.startAnimation(animation);
//        btn.setTranslationX(x2);
//        btn.setTranslationY(y2);
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

    private static final LatLng SYDNEY = new LatLng(-33.88,151.21);
    private static final LatLng MOUNTAIN_VIEW = new LatLng(37.4, -122.1);
    @Override
    public void onMapReady(GoogleMap googleMap){
        alarmsMap = new HashMap<>();
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.ic_res);

        Marker marker = mMap.addMarker(new MarkerOptions().position(sydney)
                .title("Marker in Sydney")
                .icon(icon)
        );
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

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
                if(alarmsMap.containsKey(marker.getPosition())){
                    removeTempMarker();
                    if(!marker.getTitle().equals("Edit")){
                        marker.setTitle("Edit");
                        currentStatus = EDIT;
                    }
                    else{
                        marker.setTitle("Remove");
                        currentStatus = REMOVE;
                    }
                }
                else{
                    marker.setTitle("Create");
                    currentStatus = CREATE;
                }

                Singleton.getInstance().setCurrentMarker(marker);
                notifyMe(currentStatus.toString());

                return false;


            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                switch (currentStatus){
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


    public enum status {
        CREATE, NO_MARKER, EDIT, REMOVE
    }
    private status currentStatus = NO_MARKER;

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
        currentStatus = NO_MARKER;
        handelNewMarker(point);
        moveToPosition(point);
        notifyMe(currentStatus.toString());
    }

    private void handelNewMarker(LatLng point){
        removeTempMarker();
//        deleteCurrentMarker();
        createMarker(point);
    }

    private void removeTempMarker(){
        if(Singleton.getInstance().getCurrentMarker() != null &&
                !alarmsMap.containsKey(Singleton.getInstance().getCurrentMarker().getPosition())){
            removeCurrentMarker();
        }
    }

    private void handelNoMarker(LatLng point){
        createMarker(point);
    }

    private void createMarker(LatLng point){
        Marker tmp =  mMap.addMarker(new MarkerOptions().position(point).title("Create New Alarm"));
        Singleton.getInstance().setCurrentMarker(tmp);
        FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.addButton);
//        animateBtn(addButton, 0, 100, 250);
    }

    private void removeCurrentMarker(){
        if(Singleton.getInstance().getCurrentMarker() != null){
            Singleton.getInstance().getCurrentMarker().remove();
            Singleton.getInstance().setCurrentMarker(null);
        }
        currentStatus = NO_MARKER;
    }

    private void addCurrentMarker(){
        if(Singleton.getInstance().getCurrentMarker() != null){
            EditText alarmName = (EditText) findViewById(R.id.edit_alarm_name);









            Alarm tmp = new Alarm("",null,null,100,true,Alarm.type.SINGLE, null);
            alarmsMap.put(Singleton.getInstance().getCurrentMarker().getPosition(), tmp);
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

}
