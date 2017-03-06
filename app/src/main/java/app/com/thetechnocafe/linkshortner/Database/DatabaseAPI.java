package app.com.thetechnocafe.linkshortner.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.List;

import app.com.thetechnocafe.linkshortner.Models.UrlListModels.ShortLink;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by gurleensethi on 06/03/17.
 */

public class DatabaseAPI {
    private static final String TAG = DatabaseAPI.class.getName();
    private static DatabaseAPI sInstance;
    private DatabaseHelper mDatabaseHelper;

    //Singleton class
    private DatabaseAPI(Context context) {
        mDatabaseHelper = new DatabaseHelper(context);
    }

    //Instance method
    public static DatabaseAPI getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseAPI(context);
        }
        return sInstance;
    }

    //Predicate Object to decide whether a Link already exits in the database
    private Predicate<ShortLink> shortLinkAlreadyExistsPredicate = new Predicate<ShortLink>() {
        @Override
        public boolean test(ShortLink shortLink) throws Exception {
            //Get the database
            SQLiteDatabase database = mDatabaseHelper.getReadableDatabase();

            //Create sql query to get the links with the same id
            String selectionSQL = "SELECT * FROM "
                    + DatabaseHelper.SHORT_LINK_TABLE
                    + " WHERE "
                    + DatabaseHelper.COL_SHORT_LINK_ID
                    + " = "
                    + "\"" + shortLink.getId() + "\"";

            Cursor cursor = database.rawQuery(selectionSQL, null);

            boolean result = cursor.getCount() == 0;

            //Close cursor and database
            cursor.close();
            database.close();

            return result;
        }
    };

    /**
     * Insert a Single Short Link and its Analytics in the Database
     *
     * @param shortLink ShortLink model to be stored
     */
    private void insertLink(ShortLink shortLink) {
        //Create the content values for short link and add data
        ContentValues shortLinkContentValues = new ContentValues();
        shortLinkContentValues.put(DatabaseHelper.COL_SHORT_LINK_KIND, shortLink.getKind());
        shortLinkContentValues.put(DatabaseHelper.COL_SHORT_LINK_ID, shortLink.getId());
        shortLinkContentValues.put(DatabaseHelper.COL_SHORT_LINK_LONG_URL, shortLink.getLongUrl());
        shortLinkContentValues.put(DatabaseHelper.COL_SHORT_LINK_CREATED, shortLink.getCreated());
        shortLinkContentValues.put(DatabaseHelper.COL_SHORT_LINK_STATUS, shortLink.getStatus());

        //Create the content values for analytics and add data
        ContentValues analyticsContentValues = new ContentValues();
        analyticsContentValues.put(DatabaseHelper.COL_ANALYTICS_ID, shortLink.getId());
        analyticsContentValues.put(DatabaseHelper.COL_ANALYTICS_ALL_TIME, shortLink.getAnalytics().getAllTime().getShortUrlClicks());
        analyticsContentValues.put(DatabaseHelper.COL_ANALYTICS_DAY, shortLink.getAnalytics().getDay().getShortUrlClicks());
        analyticsContentValues.put(DatabaseHelper.COL_ANALYTICS_MONTH, shortLink.getAnalytics().getMonth().getShortUrlClicks());
        analyticsContentValues.put(DatabaseHelper.COL_ANALYTICS_WEEK, shortLink.getAnalytics().getWeek().getShortUrlClicks());
        analyticsContentValues.put(DatabaseHelper.COL_ANALYTICS_TWO_HOURS, shortLink.getAnalytics().getTwoHours().getShortUrlClicks());

        //Insert in database
        //Get the database
        SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();

        database.insert(DatabaseHelper.SHORT_LINK_TABLE, null, shortLinkContentValues);
        database.insert(DatabaseHelper.ANALYTICS_TABLE, null, analyticsContentValues);

        //Close database
        database.close();

        Log.d(TAG, "Stored link : " + shortLink.getId());
    }

    //Insert a Single ShortLink on a IO thread using RxJAVA
    public void insertShortLinkAsync(ShortLink shortLink) {
        //Create an Observable the runs on IO thread to insert the data
        Observable.create((ObservableOnSubscribe<Void>) e -> {
            insertLink(shortLink);
            e.onComplete();
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    //Insert a List of ShortLink on a IO thread using RxJAVA
    public void insertShortLinkAsync(List<ShortLink> shortLinksList) {
        Observable.fromIterable(shortLinksList)
                .filter(shortLinkAlreadyExistsPredicate)
                .subscribeOn(Schedulers.io())
                .subscribe(this::insertLink);
    }
}
