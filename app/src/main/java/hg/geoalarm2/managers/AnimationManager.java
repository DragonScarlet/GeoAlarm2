package hg.geoalarm2.managers;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.gms.maps.GoogleMap;

import hg.geoalarm2.R;

/**
 * Created by dimkn on 8/18/2017.
 */

public class AnimationManager {
    public static void hideDetailsMenu(Activity activity, GoogleMap map){
        Animation bottomDown = AnimationUtils.loadAnimation(activity.getBaseContext(),
                R.anim.bottom_down);
        ViewGroup hiddenPanel = (ViewGroup)activity.findViewById(R.id.alarm_details);
        if( hiddenPanel.getVisibility() == View.VISIBLE){
            hiddenPanel.startAnimation(bottomDown);
            hiddenPanel.setVisibility(View.INVISIBLE);
            resetOffSetMap(map);
        }
    }

    public static void showDetailsMenu(Activity activity, GoogleMap map){
        Animation bottomUp = AnimationUtils.loadAnimation(activity.getBaseContext(),
                R.anim.bottom_up);
        ViewGroup hiddenPanel = (ViewGroup)activity.findViewById(R.id.alarm_details);
        if( hiddenPanel.getVisibility() == View.INVISIBLE){
            hiddenPanel.startAnimation(bottomUp);
            hiddenPanel.setVisibility(View.VISIBLE);
            offSetMap(map, hiddenPanel);
        }
    }


    private static void offSetMap(GoogleMap map, ViewGroup hiddenPanel){
        if(map != null){
            // leftPx, topPx, rightPx, bottomPx
            map.setPadding( 0 , 0, 0 , hiddenPanel.getHeight());
        }
    }

    private static void resetOffSetMap(GoogleMap map){
        if(map != null) {
            map.setPadding(0, 0, 0, 0);
        }
    }

}
