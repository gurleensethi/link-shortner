package app.com.thetechnocafe.linkshortner.AllLinks;

import app.com.thetechnocafe.linkshortner.Database.DatabaseAPI;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by gurleensethi on 09/03/17.
 */

public class AllLinksPresenter implements AllLinksContract.Presenter {

    private AllLinksContract.View mMainView;
    private CompositeDisposable compositeDisposable;

    @Override
    public void attachView(AllLinksContract.View view) {
        mMainView = view;
        mMainView.initViews();

        compositeDisposable = new CompositeDisposable();

        loadLinksFromDatabase();
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

    private void loadLinksFromDatabase() {
        Disposable disposable = DatabaseAPI.getInstance(mMainView.getAppContext())
                .getSavedShortLinks(0)
                .subscribe(shortLinks -> {
                    mMainView.onShortLinksLoaded(shortLinks);
                });

        compositeDisposable.add(disposable);
    }
}
