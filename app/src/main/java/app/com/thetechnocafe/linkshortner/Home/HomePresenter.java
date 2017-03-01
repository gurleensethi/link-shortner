package app.com.thetechnocafe.linkshortner.Home;

/**
 * Created by gurleensethi on 01/03/17.
 */

public class HomePresenter implements HomeContract.Presenter {

    private HomeContract.View mMainView;

    @Override
    public void attachView(HomeContract.View view) {
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
