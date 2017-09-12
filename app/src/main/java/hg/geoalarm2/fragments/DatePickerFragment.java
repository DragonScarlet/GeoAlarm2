package hg.geoalarm2.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Button;
import android.widget.DatePicker;

import hg.geoalarm2.objects.time.Date;
import hg.geoalarm2.R;
import hg.geoalarm2.utils.Singleton;
import hg.geoalarm2.enums.State;

/**
 * Created by dimkn on 8/23/2017.
 */

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        Date date = new Date(year, month+1, day);
        if(Singleton.getInstance().getCurrentState().equals(State.CREATE_START_DAY)){
            Singleton.getInstance().getCacheAlarm().setStartDay(date);
            Button btn = Singleton.getInstance().getMainActivity().findViewById(R.id.button_start_day);
            btn.setText(date.getStrDate());
        }
        else{
            Singleton.getInstance().getCacheAlarm().setEndDay(date);
            Button btn = Singleton.getInstance().getMainActivity().findViewById(R.id.button_end_day);
            btn.setText(date.getStrDate());
        }
    }
}