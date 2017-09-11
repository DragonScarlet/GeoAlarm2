package hg.geoalarm2;

/**
 * Created by dimkn on 8/25/2017.
 */

public class Time {
    int hour;
    int minutes;

    public Time(int hour, int minutes) {
        this.hour = hour;
        this.minutes = minutes;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public String getStrTime(){
        return  convertDateIntToString(hour) + ":" +  convertDateIntToString(minutes) + ":00";
    }

    private String convertDateIntToString(int date){
        String strDay;
        if(date < 10){
            strDay = "0" + date;
        }
        else{
            strDay = "" + date;
        }
        return strDay;
    }
}
