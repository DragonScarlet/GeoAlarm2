package hg.geoalarm2.managers;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by dimkn on 11/21/2017.
 */

public class MenuManager {


    public static void notifyMe(String msg, Context context) {
        CharSequence text = msg;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
