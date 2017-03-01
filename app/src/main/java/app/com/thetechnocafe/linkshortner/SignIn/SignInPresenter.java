package app.com.thetechnocafe.linkshortner.SignIn;

import app.com.thetechnocafe.linkshortner.Utilities.AuthPreferences;

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
        //Check if logged in
        if (AuthPreferences.getInstance().getAccountName(mMainView.getAppContext()) != null) {
            mMainView.startHomeActivity();
        }
    }

    @Override
    public void onPause() {

    }

    @Override
    public void saveAccountAndToken(String account, String token) {
        //Save the account and token
        AuthPreferences.getInstance()
                .setAccountName(mMainView.getAppContext(), account)
                .setAuthToken(mMainView.getAppContext(), token);

        //Start the home activity
        mMainView.startHomeActivity();
    }
}
