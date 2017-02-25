package app.com.thetechnocafe.linkshortner.SignIn;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import app.com.thetechnocafe.linkshortner.LinkWithoutAccount.LinkWithoutAccountActivity;
import app.com.thetechnocafe.linkshortner.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SignInActivity extends AppCompatActivity implements SignInContract.View {

    @BindView(R.id.shorten_link_without_account_text_view)
    TextView mShortenLinkWithoutAccountTextView;

    private SignInContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        ButterKnife.bind(this);

        mPresenter = new SignInPresenter();
        mPresenter.attachView(this);
    }

    @Override
    public Context getAppContext() {
        return getApplicationContext();
    }

    @Override
    public void initViews() {
        mShortenLinkWithoutAccountTextView.setOnClickListener(view -> {
            //Launch LinkWithoutAccountActivity
            startActivity(LinkWithoutAccountActivity.getIntent(getApplicationContext()));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }
}
