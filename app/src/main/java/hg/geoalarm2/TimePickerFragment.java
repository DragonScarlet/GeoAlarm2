package hg.geoalarm2;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.app.TimePickerDialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.TimePicker;

import java.sql.Date;

import static hg.geoalarm2.State.CREATE;
import static hg.geoalarm2.State.CREATE_START_TIME;

/**
 * Created by dimkn on 8/23/2017.
 */

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        Time time = new Time(hourOfDay, minute);
        if(Singleton.getInstance().getCurrentState().equals(CREATE_START_TIME)){
            Singleton.getInstance().getCacheAlarm().setStartTime(time);
            Button btn = Singleton.getInstance().getMainActivity().findViewById(R.id.button_start_time);
            btn.setText(time.getStrTime());
        }
        else{
            Singleton.getInstance().getCacheAlarm().setEndTime(time);
            Button btn = Singleton.getInstance().getMainActivity().findViewById(R.id.button_end_time);
            btn.setText(time.getStrTime());
        }
        Singleton.getInstance().setCurrentState(CREATE);
    }
}