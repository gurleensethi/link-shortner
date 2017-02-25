package app.com.thetechnocafe.linkshortner.LinkWithoutAccount;

import app.com.thetechnocafe.linkshortner.BaseMVP;

/**
 * Created by gurleensethi on 25/02/17.
 */

public class LinkWithoutAccountContract {
    public interface View extends BaseMVP.View {
        void onLinkShortened(String shortUrl, String longUrl);

        void onLinkShortenedError(String error);
    }

    public interface Presenter extends BaseMVP.Presenter<LinkWithoutAccountContract.View> {
        void shortenLink(String longUrl);
    }
}
