package app.com.thetechnocafe.linkshortner.SignIn;

/**
 * Created by gurleensethi on 25/02/17.
 */

public class SignInPresenter implements SignInContract.Presenter {

    private SignInContract.View mMainView;

    @Override
    public void attachView(SignInContract.View view) {
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
