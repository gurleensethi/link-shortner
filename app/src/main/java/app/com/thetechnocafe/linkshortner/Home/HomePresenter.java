package app.com.thetechnocafe.linkshortner.Home;

import android.util.Log;

import app.com.thetechnocafe.linkshortner.Database.DatabaseAPI;
import app.com.thetechnocafe.linkshortner.Models.LongLinkPOSTModel;
import app.com.thetechnocafe.linkshortner.Models.UrlListModels.Analytics;
import app.com.thetechnocafe.linkshortner.Models.UrlListModels.ShortLink;
import app.com.thetechnocafe.linkshortner.Networking.NetworkService;
import app.com.thetechnocafe.linkshortner.Utilities.AuthPreferences;
import app.com.thetechnocafe.linkshortner.Utilities.Constants;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by gurleensethi on 01/03/17.
 */

public class HomePresenter implements HomeContract.Presenter {
    private static final String TAG = HomePresenter.class.getSimpleName();
    private HomeContract.View mMainView;
    private CompositeDisposable compositeDisposable;

    @Override
    public void attachView(HomeContract.View view) {
        mMainView = view;
        mMainView.initViews();

        //Generate new token
        mMainView.requestNewToken(
                AuthPreferences.getInstance().getAuthToken(mMainView.getAppContext()),
                AuthPreferences.getInstance().getAccountName(mMainView.getAppContext())
        );

        compositeDisposable = new CompositeDisposable();

        //Start the swipe refresh layout
        mMainView.startRefreshing();
    }

    @Override
    public void detachView() {
        mMainView = null;

        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void saveNewToken(String token) {
        //Save the new token
        AuthPreferences.getInstance().setAuthToken(mMainView.getAppContext(), token);

        loadLinksFromDatabase();
    }

    @Override
    public void signOut() {
        //Remove the account name
        AuthPreferences.getInstance().setAccountName(mMainView.getAppContext(), null);

        //Delete all the data
        DatabaseAPI.getInstance(mMainView.getAppContext()).deleteAllData();
    }

    @Override
    public void shortenUrl(String longUrl) {
        //Create long url model
        LongLinkPOSTModel longLinkPOSTModel = new LongLinkPOSTModel(longUrl);

        Disposable disposable = NetworkService.getInstance()
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

                    loadLinksFromDatabase();

                    mMainView.onLinkShortened(shortenedLinkModel.getId(), shortenedLinkModel.getLongUrl());
                }, throwable -> {
                    mMainView.onLinkShortenError();
                    throwable.printStackTrace();
                });

        compositeDisposable.add(disposable);
    }

    @Override
    public void reloadLinks() {
        loadLinksFromDatabase();
    }

    private void loadLinksFromDatabase() {
        //Create a disposable to get all the links from database
        Disposable disposable = DatabaseAPI.getInstance(mMainView.getAppContext())
                .getSavedShortLinks(Constants.MAX_HOME_SCREEN_LINKS)
                .subscribe(shortLinks -> {
                    mMainView.onShortLinksReceived(shortLinks);
                    loadLinksFromNetwork();
                });

        compositeDisposable.add(disposable);
    }

    private void loadLinksFromNetwork() {
        //Create a disposable to get all the links from network
        Disposable disposable = NetworkService.getInstance()
                .getAllLinksFromAPI(AuthPreferences.getInstance().getAuthToken(mMainView.getAppContext()), "FULL")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shortLinks -> {
                    mMainView.onShortLinksReceived(shortLinks.size() >= Constants.MAX_HOME_SCREEN_LINKS ? shortLinks.subList(0, Constants.MAX_HOME_SCREEN_LINKS) : shortLinks);

                    //Update the database with new links
                    Disposable insertDataDisposable = DatabaseAPI.getInstance(mMainView.getAppContext())
                            .insertShortLinkAsync(shortLinks)
                            .subscribe(
                                    o -> {
                                    },
                                    throwable -> {
                                        ((Exception) throwable).printStackTrace();
                                        Log.d(TAG, "Error while inserting data in Database : " + throwable.toString());
                                        mMainView.stopRefreshing();
                                    },
                                    () -> {
                                        mMainView.stopRefreshing();
                                        loadTotalClicks();
                                        loadTotalShortenedLinks();
                                    }
                            );

                    compositeDisposable.add(insertDataDisposable);
                }, throwable -> {
                    loadTotalClicks();
                    loadTotalShortenedLinks();
                });

        compositeDisposable.add(disposable);
    }

    private void loadTotalClicks() {
        //Create a disposable to get total number of clicks
        Disposable disposable = DatabaseAPI.getInstance(mMainView.getAppContext())
                .getTotalClicks()
                .subscribe(count -> {
                    mMainView.setTotalClicks(count);
                });

        compositeDisposable.add(disposable);
    }

    private void loadTotalShortenedLinks() {
        //Create a disposable to get total number of clicks
        Disposable disposable = DatabaseAPI.getInstance(mMainView.getAppContext())
                .getTotalShortenedLinks()
                .subscribe(count -> {
                    mMainView.setTotalShortenedLinks(count);
                });

        compositeDisposable.add(disposable);
    }
}
