package app.com.thetechnocafe.linkshortner.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by gurleensethi on 06/03/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "links_db";
    private static final int DB_VERSION = 1;

    //Columns for ShortLinkModel
    public static final String SHORT_LINK_TABLE = "table_short_links";
    public static final String COL_SHORT_LINK_KIND = "col_kind";
    public static final String COL_SHORT_LINK_ID = "col_id";
    public static final String COL_SHORT_LINK_LONG_URL = "col_long_url";
    public static final String COL_SHORT_LINK_STATUS = "col_status";
    public static final String COL_SHORT_LINK_CREATED = "col_created";

    //Columns for Analytics
    public static final String ANALYTICS_TABLE = "table_analytics";
    public static final String COL_ANALYTICS_ID = "id";
    public static final String COL_ANALYTICS_ALL_TIME = "col_all_time";
    public static final String COL_ANALYTICS_MONTH = "col_month";
    public static final String COL_ANALYTICS_WEEK = "col_week";
    public static final String COL_ANALYTICS_DAY = "col_day";
    public static final String COL_ANALYTICS_TWO_HOURS = "col_two_hours";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Create the ShortLink Table
        String shortLinkSQL = "CREATE TABLE " + SHORT_LINK_TABLE
                + "(" + COL_SHORT_LINK_KIND + " VARCHAR, "
                + COL_SHORT_LINK_ID + " VARCHAR PRIMARY KEY, "
                + COL_SHORT_LINK_LONG_URL + " VARCHAR, "
                + COL_SHORT_LINK_STATUS + " VARCHAR, "
                + COL_SHORT_LINK_CREATED + " VARCHAR);";

        sqLiteDatabase.execSQL(shortLinkSQL);

        //Create the analytics table
        String analyticsSQL = "CREATE TABLE " + ANALYTICS_TABLE
                + "(" + COL_ANALYTICS_ALL_TIME + " VARCHAR, "
                + COL_ANALYTICS_ID + " VARCHAR PRIMARY KEY, "
                + COL_ANALYTICS_MONTH + " VARCHAR, "
                + COL_ANALYTICS_WEEK + " VARCHAR, "
                + COL_ANALYTICS_DAY + " VARCHAR, "
                + COL_ANALYTICS_TWO_HOURS + " VARCHAR);";

        sqLiteDatabase.execSQL(analyticsSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
