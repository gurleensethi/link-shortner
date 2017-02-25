package app.com.thetechnocafe.linkshortner.LinkWithoutAccount;

/**
 * Created by gurleensethi on 25/02/17.
 */

public class LinkWithoutAccountPresenter implements LinkWithoutAccountContract.Presenter {

    private LinkWithoutAccountContract.View mMainView;

    @Override
    public void attachView(LinkWithoutAccountContract.View view) {
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
}
