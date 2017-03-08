package app.com.thetechnocafe.linkshortner.AllLinks;

import java.util.List;

import app.com.thetechnocafe.linkshortner.BaseMVP;
import app.com.thetechnocafe.linkshortner.Models.UrlListModels.ShortLink;

/**
 * Created by gurleensethi on 09/03/17.
 */

public class AllLinksContract {
    public interface View extends BaseMVP.View {
        void onShortLinksLoaded(List<ShortLink> shortLinks);
    }

    public interface Presenter extends BaseMVP.Presenter<AllLinksContract.View> {

    }
}
