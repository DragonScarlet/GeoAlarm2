package hg.geoalarm2.receivers;

import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.util.Log;

import hg.geoalarm2.activities.AlarmAdsActivity;
import hg.geoalarm2.activities.AlarmPopUpActivity;

/**
 * Created by dimkn on 9/5/2017.
 */

public class AlarmReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent){
        Log.e("Solider 76", "I got you in my sight!");

//        Intent service_intent = new Intent(context, RingtonePlayingService.class);

        Intent popUpIntent = new Intent(context, AlarmAdsActivity.class);

        context.startActivity(popUpIntent);
        //context.startService(service_intent);
    }
}
