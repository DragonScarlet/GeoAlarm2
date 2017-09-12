package hg.geoalarm2.managers;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import hg.geoalarm2.R;

/**
 * Created by dimkn on 8/18/2017.
 */

public class AnimationManager {
    public static void hideDetailsMenu(Activity activity){
        Animation bottomDown = AnimationUtils.loadAnimation(activity.getBaseContext(),
                R.anim.bottom_down);
        ViewGroup hiddenPanel = (ViewGroup)activity.findViewById(R.id.alarm_details);
        if( hiddenPanel.getVisibility() == View.VISIBLE){
            hiddenPanel.startAnimation(bottomDown);
            hiddenPanel.setVisibility(View.INVISIBLE);
        }
    }

    public static void showDetailsMenu(Activity activity){
        Animation bottomUp = AnimationUtils.loadAnimation(activity.getBaseContext(),
                R.anim.bottom_up);
        ViewGroup hiddenPanel = (ViewGroup)activity.findViewById(R.id.alarm_details);
        if( hiddenPanel.getVisibility() == View.INVISIBLE){
            hiddenPanel.startAnimation(bottomUp);
            hiddenPanel.setVisibility(View.VISIBLE);
        }

    }

}
