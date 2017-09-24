package hg.geoalarm2.utils;

import android.icu.text.SimpleDateFormat;

import java.text.ParseException;

import hg.geoalarm2.objects.alarm.Alarm;
import hg.geoalarm2.objects.time.Date;
import hg.geoalarm2.objects.time.Time;

/**
 * Created by dimkn on 9/18/2017.
 */

public class DateUtils {

    public static long getDateMillis(Date date, Time time){
        long millis = -1;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            String strDate = date. getStrDate() + " " + time.getStrTime();
            java.util.Date parsedDate = sdf.parse(strDate);
            millis = parsedDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return millis;
    }

    public static long getTimeDifference(Alarm alarm){
        long start = getDateMillis(alarm.getStartDay(),alarm.getStartTime());
        long end = getDateMillis(alarm.getEndDay(),alarm.getEndTime());
        return end - start;
    }

}
