package app.com.thetechnocafe.linkshortner.Services.ClipboardChangeListener;

import app.com.thetechnocafe.linkshortner.BaseMVP;

/**
 * Created by gurleensethi on 27/03/17.
 */

public class ClipboardChangeListenerContract {
    public interface View extends BaseMVP.View {

    }

    public interface Presenter extends BaseMVP.Presenter<ClipboardChangeListenerContract.View> {

    }
}
