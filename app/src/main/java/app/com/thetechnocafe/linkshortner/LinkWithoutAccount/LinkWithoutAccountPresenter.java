package app.com.thetechnocafe.linkshortner.LinkWithoutAccount;

import app.com.thetechnocafe.linkshortner.Models.LongLinkPOSTModel;
import app.com.thetechnocafe.linkshortner.Networking.NetworkService;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by gurleensethi on 25/02/17.
 */

public class LinkWithoutAccountPresenter implements LinkWithoutAccountContract.Presenter {

    private LinkWithoutAccountContract.View mMainView;
    private CompositeDisposable compositeDisposable;

    @Override
    public void attachView(LinkWithoutAccountContract.View view) {
        mMainView = view;
        mMainView.initViews();

        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void detachView() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }

        mMainView = null;
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void shortenLink(String longUrl) {
        //Create POST model using the link
        LongLinkPOSTModel longLink = new LongLinkPOSTModel(longUrl);

        //Get the Network Service and subscribe to the result
        Disposable shortenedLinkDisposable = NetworkService.getInstance()
                .getLinkShortenerAPI()
                .getShortenedLink(longLink)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(shortenedLinkModel -> mMainView.onLinkShortened(shortenedLinkModel.getId(), shortenedLinkModel.getLongUrl()),
                        throwable -> mMainView.onLinkShortenedError(throwable.toString())
                );

        //Add disposable to composite disposable
        compositeDisposable.add(shortenedLinkDisposable);
    }
}
