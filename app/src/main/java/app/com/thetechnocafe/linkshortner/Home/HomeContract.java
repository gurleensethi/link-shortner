package app.com.thetechnocafe.linkshortner.Home;

import app.com.thetechnocafe.linkshortner.BaseMVP;

/**
 * Created by gurleensethi on 01/03/17.
 */

public class HomeContract {
    public interface View extends BaseMVP.View {

    }

    public interface Presenter extends BaseMVP.Presenter<HomeContract.View> {

    }
}
