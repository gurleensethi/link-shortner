package app.com.thetechnocafe.linkshortner.Services.ClipboardChangeListener;

import app.com.thetechnocafe.linkshortner.BaseMVP;

/**
 * Created by gurleensethi on 27/03/17.
 */

public class ClipboardChangeListenerContract {
    public interface View extends BaseMVP.View {
        void onLinkShortened(String shortLink);

        void requestTokenAndShortenLink(String oldToken, String accountName);
    }

    public interface Presenter extends BaseMVP.Presenter<ClipboardChangeListenerContract.View> {
        void saveTokenAndShortenLink(String token, String longUrl);

        void requestNewTokenAndShortenLink();
    }
}
