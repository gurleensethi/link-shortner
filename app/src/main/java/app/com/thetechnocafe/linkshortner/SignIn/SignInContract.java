package app.com.thetechnocafe.linkshortner.SignIn;

import app.com.thetechnocafe.linkshortner.BaseMVP;

/**
 * Created by gurleensethi on 25/02/17.
 */

public class SignInContract {
    public interface View extends BaseMVP.View {
        void startHomeActivity();
    }

    public interface Presenter extends BaseMVP.Presenter<SignInContract.View> {
        void saveAccountAndToken(String account, String token);
    }
}
