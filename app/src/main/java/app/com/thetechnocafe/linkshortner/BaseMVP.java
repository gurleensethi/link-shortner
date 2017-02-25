package app.com.thetechnocafe.linkshortner;

import android.content.Context;

/**
 * Created by gurleensethi on 25/02/17.
 */

public class BaseMVP {

    public interface View {
        Context getAppContext();

        void initViews();
    }

    public interface Presenter<T> {
        void attachView(T view);

        void detachView();

        void onResume();

        void onPause();
    }
}
