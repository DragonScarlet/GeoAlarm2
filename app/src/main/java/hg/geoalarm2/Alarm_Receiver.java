package hg.geoalarm2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by dimkn on 9/5/2017.
 */

public class Alarm_Receiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent){
        Log.e("Solider 76", "I got you in my sight!");

        Intent service_intent = new Intent(context, RingtonePlayingService.class);

        context.startService(service_intent);
    }
}
