package app.com.thetechnocafe.linkshortner.SignIn;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import app.com.thetechnocafe.linkshortner.R;

public class SignInActivity extends AppCompatActivity implements SignInContract.View {

    private SignInContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mPresenter = new SignInPresenter();
        mPresenter.attachView(this);
    }

    @Override
    public Context getAppContext() {
        return getApplicationContext();
    }

    @Override
    public void initViews() {

    }
}
