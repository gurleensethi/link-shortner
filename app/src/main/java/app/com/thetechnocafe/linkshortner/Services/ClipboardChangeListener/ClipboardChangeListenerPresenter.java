package app.com.thetechnocafe.linkshortner.Services.ClipboardChangeListener;

import app.com.thetechnocafe.linkshortner.Database.DatabaseAPI;
import app.com.thetechnocafe.linkshortner.Models.LongLinkPOSTModel;
import app.com.thetechnocafe.linkshortner.Models.UrlListModels.Analytics;
import app.com.thetechnocafe.linkshortner.Models.UrlListModels.ShortLink;
import app.com.thetechnocafe.linkshortner.Networking.NetworkService;
import app.com.thetechnocafe.linkshortner.Utilities.AuthPreferences;
import app.com.thetechnocafe.linkshortner.Utilities.Constants;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by gurleensethi on 27/03/17.
 */

public class ClipboardChangeListenerPresenter implements ClipboardChangeListenerContract.Presenter {

    private ClipboardChangeListenerContract.View mMainView;

    @Override
    public void attachView(ClipboardChangeListenerContract.View view) {
        mMainView = view;
        mMainView.initViews();
    }

    @Override
    public void detachView() {
        mMainView = null;
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void saveTokenAndShortenLink(String token, String longUrl) {
        //Save new token
        AuthPreferences.getInstance().setAuthToken(mMainView.getAppContext(), token);

        LongLinkPOSTModel longLinkPOSTModel = new LongLinkPOSTModel(longUrl);

        //Send the short url request
        NetworkService.getInstance()
                .getLinkShortenerAPI()
                .getShortenedLinkWithAuth(AuthPreferences.getInstance().getAuthToken(mMainView.getAppContext()), Constants.APPLICATION_JSON, longLinkPOSTModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shortenedLinkModel -> {
                    //Create a short link model
                    ShortLink shortLink = new ShortLink();
                    shortLink.setId(shortenedLinkModel.getId());
                    shortLink.setKind(shortenedLinkModel.getKind());
                    shortLink.setLongUrl(shortenedLinkModel.getLongUrl());
                    shortLink.setAnalytics(new Analytics());

                    //Add the shorten url to database
                    DatabaseAPI.getInstance(mMainView.getAppContext())
                            .insertShortLinkAsync(shortLink);

                    mMainView.onLinkShortened(shortLink.getId());
                }, throwable -> {
                    throwable.printStackTrace();
                });
    }

    @Override
    public void requestNewTokenAndShortenLink() {
        mMainView.requestTokenAndShortenLink(
                AuthPreferences.getInstance().getAuthToken(mMainView.getAppContext()),
                AuthPreferences.getInstance().getAccountName(mMainView.getAppContext())
        );
    }
}
