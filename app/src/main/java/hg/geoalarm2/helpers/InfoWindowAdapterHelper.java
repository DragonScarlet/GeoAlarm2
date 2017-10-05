package hg.geoalarm2.helpers;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;

import hg.geoalarm2.enums.State;
import hg.geoalarm2.objects.alarm.Alarm;
import hg.geoalarm2.utils.MapUtils;
import hg.geoalarm2.utils.Singleton;

/**
 * Created by dimkn on 10/4/2017.
 */

public class InfoWindowAdapterHelper {
    Context mContext;

    public InfoWindowAdapterHelper(Context context) {
        mContext = context;
    }

    public LinearLayout getInfoTextLayout(Marker marker){
        Alarm alarm = Singleton.getInstance().getAlarmsMap().get(MapUtils.getMapKey(marker));
        LinearLayout info = getDefaultInfoLayout(marker);
        if(alarm != null){
            fillAlarmInformation(alarm, info);
        }
        return info;
    }

    private LinearLayout getDefaultInfoLayout(Marker marker){
        LinearLayout info = new LinearLayout(mContext);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setGravity(Gravity.CENTER);
        info.addView(getTitleInformation(marker));
        return info;
    }

    private TextView getTitleInformation(Marker marker){
        TextView title = new TextView(mContext);
        title.setTextColor(Color.BLACK);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(null, Typeface.BOLD);
        title.setText(marker.getTitle());
        return title;
    }

    private void fillAlarmInformation(Alarm alarm, LinearLayout info){
        info.addView(getTime(alarm, true));
        info.addView(getTime(alarm, false));
        info.addView(getStatus(alarm));
        info.addView(getAction());
    }

    private TextView getTime(Alarm alarm, boolean isStart){
        TextView snippet = new TextView(mContext);
        snippet.setTextColor(Color.GRAY);
        if(isStart){
            snippet.setText("Start: " + alarm.getStartDay().getStrDate() + " " + alarm.getStartTime().getStrTime());
        }
        else{
            snippet.setText("End: " + alarm.getEndDay().getStrDate() + " " + alarm.getEndTime().getStrTime());
        }
        snippet.setGravity(Gravity.CENTER);
        return snippet;
    }

    private TextView getStatus(Alarm alarm){
        TextView snippet = new TextView(mContext);
        snippet.setGravity(Gravity.CENTER);
        if(alarm.isActive()){
            snippet.setTextColor(Color.GREEN);
            snippet.setText("On");
        }
        else{
            snippet.setTextColor(Color.RED);
            snippet.setText("Off");
        }
        return snippet;
    }

    private TextView getAction(){
        TextView snippet = new TextView(mContext);
        snippet.setGravity(Gravity.CENTER);
        snippet.setTypeface(null, Typeface.BOLD);
        if( Singleton.getInstance().getCurrentState().equals(State.EDIT)){
            snippet.setText("Edit");
        }
        else {
            if (Singleton.getInstance().getCurrentState().equals(State.REMOVE)) {
                snippet.setText("Remove");
            } else {
                snippet.setText("Change Status");
            }
    }
        return snippet;
    }
}
