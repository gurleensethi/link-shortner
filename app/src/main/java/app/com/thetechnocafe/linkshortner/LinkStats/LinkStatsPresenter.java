package app.com.thetechnocafe.linkshortner.LinkStats;

import app.com.thetechnocafe.linkshortner.Networking.NetworkService;
import app.com.thetechnocafe.linkshortner.Utilities.Constants;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by gurleensethi on 19/03/17.
 */

public class LinkStatsPresenter implements LinkStatsContract.Presenter {

    private LinkStatsContract.View mMainView;
    private CompositeDisposable compositeDisposable;

    @Override
    public void attachView(LinkStatsContract.View view) {
        mMainView = view;
        mMainView.initViews();

        compositeDisposable = new CompositeDisposable();
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
    public void loadStatsForUrl(String shortUrl) {
        Disposable disposable = NetworkService.getInstance()
                .getLinkShortenerAPI()
                .getStatsForShortLink(Constants.API_KEY, shortUrl, "FULL")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(statsModel -> mMainView.onLoadStats(statsModel));

        compositeDisposable.add(disposable);
    }
}
