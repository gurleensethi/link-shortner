package app.com.thetechnocafe.linkshortner.LinkWithoutAccount;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import app.com.thetechnocafe.linkshortner.R;

public class LinkWithoutAccountActivity extends AppCompatActivity implements LinkWithoutAccountContract.View {

    private LinkWithoutAccountContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_without_account);

        mPresenter = new LinkWithoutAccountPresenter();
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
