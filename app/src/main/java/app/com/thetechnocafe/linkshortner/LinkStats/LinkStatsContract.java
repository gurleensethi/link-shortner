package app.com.thetechnocafe.linkshortner.LinkStats;

import app.com.thetechnocafe.linkshortner.BaseMVP;
import app.com.thetechnocafe.linkshortner.Models.LinkStatsModel.StatsModel;

/**
 * Created by gurleensethi on 19/03/17.
 */

public class LinkStatsContract {
    public interface View extends BaseMVP.View {
        void onLoadStats(StatsModel stats);

        void onNetworkError();
    }

    public interface Presenter extends BaseMVP.Presenter<LinkStatsContract.View> {
        void loadStatsForUrl(String shortUrl);
    }
}
