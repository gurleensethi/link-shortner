package app.com.thetechnocafe.linkshortner.Home;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import app.com.thetechnocafe.linkshortner.Adapters.LinksRecyclerAdapter;
import app.com.thetechnocafe.linkshortner.Models.UrlListModels.ShortLink;
import app.com.thetechnocafe.linkshortner.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements HomeContract.View {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.links_recycler_view)
    RecyclerView mLinksRecyclerView;
    @BindView(R.id.no_shortened_links_text_view)
    TextView mNoShortenedLinksTextView;

    private HomeContract.Presenter mPresenter;
    private LinksRecyclerAdapter mLinksRecyclerAdapter;
    private AccountManager mAccountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        mAccountManager = AccountManager.get(this);

        mPresenter = new HomePresenter();
        mPresenter.attachView(this);
    }

    @Override
    public Context getAppContext() {
        return getApplicationContext();
    }

    @Override
    public void initViews() {
        //Configure toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.home);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);

        //Configure recycler view
        mLinksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mLinksRecyclerView.setNestedScrollingEnabled(false);

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

    @Override
    public void requestNewToken(String token, String accountName) {
        requestToken(token, accountName);
    }

    @Override
    public void onShortLinksReceived(List<ShortLink> shortLinks) {
        //Check if links are available
        if (shortLinks.size() > 0) {
            mLinksRecyclerView.setVisibility(View.VISIBLE);
            mNoShortenedLinksTextView.setVisibility(View.GONE);

            //Send data to recycler view
            setUpOrRefreshRecyclerView(shortLinks);
        } else {
            mLinksRecyclerView.setVisibility(View.GONE);
            mNoShortenedLinksTextView.setVisibility(View.VISIBLE);
        }
    }

    private void setUpOrRefreshRecyclerView(List<ShortLink> shortLinks) {
        if (mLinksRecyclerAdapter == null) {
            mLinksRecyclerAdapter = new LinksRecyclerAdapter(this, shortLinks);
            mLinksRecyclerView.setAdapter(mLinksRecyclerAdapter);
        } else {
            mLinksRecyclerAdapter.notifyDataSetChanged();
        }
    }

    //Request the OAuth Token
    private void requestToken(String oldToken, String accountName) {
        //Temporary account
        Account selectedAccount = null;

        //Retrieve the selected account account
        for (Account account : mAccountManager.getAccountsByType("com.google")) {
            if (account.name.equals(accountName)) {
                selectedAccount = account;
            }
        }

        mAccountManager.invalidateAuthToken("com.google", oldToken);

        //Get the Auth token for the selected account
        mAccountManager.getAuthToken(selectedAccount, "oauth2:https://www.googleapis.com/auth/urlshortener", null, this, accountManagerFuture -> {
            try {
                //Get the bundle
                Bundle bundle = accountManagerFuture.getResult();

                //Get the intent from bundle
                Intent intent = (Intent) bundle.get(AccountManager.KEY_INTENT);

                if (intent != null) {
                    //startActivityForResult(intent, RC_AGAIN_TOKEN);
                } else {
                    String token = bundle.getString(AccountManager.KEY_AUTHTOKEN);

                    //Save the account and token
                    mPresenter.saveNewToken(token);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, null);
    }

}
