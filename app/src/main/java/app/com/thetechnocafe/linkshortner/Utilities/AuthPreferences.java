package app.com.thetechnocafe.linkshortner.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by gurleensethi on 01/03/17.
 */

public class AuthPreferences {
    private static AuthPreferences sInstance;
    private static final String SP_FILE_NAME = "sp_auth";
    private static final String SP_AUTH_TOKEN = "auth_token";
    private static final String SP_ACCOUNT_NAME = "account_name";

    //Singleton class
    private AuthPreferences() {

    }

    //Instance method
    public static AuthPreferences getInstance() {
        if (sInstance == null) {
            sInstance = new AuthPreferences();
        }
        return sInstance;
    }

    //Get shared preferences
    private SharedPreferences getSharedPreferenes(Context context) {
        return context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
    }

    //Get the shared preferences editor
    private SharedPreferences.Editor getSharedPreferencesEditor(Context context) {
        return context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE).edit();
    }

    //Save the auth token
    public AuthPreferences setAuthToken(Context context, String token) {
        getSharedPreferencesEditor(context)
                .putString(SP_AUTH_TOKEN, token)
                .commit();

        return sInstance;
    }

    //Get the auth token
    public String getAuthToken(Context context) {
        return getSharedPreferenes(context).getString(SP_AUTH_TOKEN, null);
    }

    //Save the account name
    public AuthPreferences setAccountName(Context context, String name) {
        getSharedPreferencesEditor(context)
                .putString(SP_ACCOUNT_NAME, name)
                .commit();

        return sInstance;
    }

    //Get the account name
    public String getAccountName(Context context) {
        return getSharedPreferenes(context)
                .getString(SP_ACCOUNT_NAME, null);
    }

    //Invalidate the account name and token
    public void invalidateAuth(Context context) {
        getSharedPreferencesEditor(context)
                .putString(SP_ACCOUNT_NAME, null)
                .putString(SP_AUTH_TOKEN, null);
    }
}
