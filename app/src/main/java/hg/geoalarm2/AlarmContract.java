package hg.geoalarm2;

import android.provider.BaseColumns;

/**
 * Created by dimkn on 8/29/2017.
 */

public class AlarmContract {

    private AlarmContract(){}

    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "alarm";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_START_TIME = "startTime";
        public static final String COLUMN_NAME_END_TIME = "endTime";
        public static final String COLUMN_NAME_START_DATE = "startDate";
        public static final String COLUMN_NAME_END_DATE = "endDate";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
        public static final String COLUMN_NAME_AREA = "area";
    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_NAME_TITLE + " TEXT," +
                    FeedEntry.COLUMN_NAME_START_TIME + " DATE," +
                    FeedEntry.COLUMN_NAME_END_TIME + " DATE," +
                    FeedEntry.COLUMN_NAME_START_DATE + " DATE," +
                    FeedEntry.COLUMN_NAME_END_DATE + " DATE," +
                    FeedEntry.COLUMN_NAME_LATITUDE + " FLOAT," +
                    FeedEntry.COLUMN_NAME_LONGITUDE + " FLOAT," +
                    FeedEntry.COLUMN_NAME_AREA + " FLOAT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;
}
