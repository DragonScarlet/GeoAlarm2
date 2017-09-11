package hg.geoalarm2;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * Created by dimkn on 8/25/2017.
 */

public class DataManager {
    public static void saveAlarms(){
        saveHashMap( "Alarms", Singleton.getInstance().getAlarmsMap());
    }

    public static  HashMap<String, Alarm> getAlarms(){
        return getHashMap("Alarms");
    }

    public static void saveHashMap(String key , HashMap<String, Alarm> map) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Singleton.getInstance().getMainActivity());
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        MapWrapper wrapper = new MapWrapper();
        wrapper.setMap(map);
        String serializedMap = gson.toJson(wrapper);
        editor.putString(key,serializedMap);
        editor.apply();
    }

    public static HashMap<String, Alarm> getHashMap(String key) {
        try {
            Gson gson = new Gson();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Singleton.getInstance().getMainActivity());
            Type type = new TypeToken<MapWrapper>(){}.getType();
            String json = prefs.getString(key,"");
            MapWrapper wrapper = gson.fromJson(json, type);
            if( wrapper.getMap() != null){
                return wrapper.getMap();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return new  HashMap<>();
    }
}