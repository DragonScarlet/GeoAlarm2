package hg.geoalarm2.objects.time;

/**
 * Created by dimkn on 8/25/2017.
 */

public class Date {
    int year;
    int month;
    int day;

    public Date(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getStrDate(){
        return convertDateIntToString(day) + "." + convertDateIntToString(month) + "." + year;
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
