package app.com.thetechnocafe.linkshortner.SignIn;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import app.com.thetechnocafe.linkshortner.LinkWithoutAccount.LinkWithoutAccountActivity;
import app.com.thetechnocafe.linkshortner.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SignInActivity extends AppCompatActivity implements SignInContract.View {

    @BindView(R.id.shorten_link_without_account_text_view)
    TextView mShortenLinkWithoutAccountTextView;
    @BindView(R.id.sign_in_button)
    Button mSignInButton;

    private static final int RC_ACCOUNT = 1;
    private static final int RC_ACCOUNT_PERMISSION = 2;
    private static final int RC_AGAIN_TOKEN = 3;
    private SignInContract.Presenter mPresenter;
    private AccountManager mAccountManager;
    private String mAccountName;
    private String mToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        ButterKnife.bind(this);

        mAccountManager = AccountManager.get(this);

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

        mSignInButton.setOnClickListener(view -> {
            //Start account selection intent
            Intent intent = AccountManager.newChooseAccountIntent(null, null, new String[]{"com.google"}, false, null, null, null, null);
            startActivityForResult(intent, RC_ACCOUNT);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Check for request code
        if (requestCode == RC_ACCOUNT) {
            //Check if result is ok
            if (resultCode == RESULT_OK) {
                //Get the selected account name
                mAccountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                requestToken();
            }
        } else if (requestCode == RC_AGAIN_TOKEN) {
            //Check if result is ok
            if (resultCode == RESULT_OK) {
                //Request token again
                requestToken();
            }
        }
    }

    //Request the OAuth Token
    private void requestToken() {
        //Temporary account
        Account selectedAccount = null;

        //Check for permissions
        if (checkForAccountPermission()) {

            //Retrieve the selected account account
            for (Account account : mAccountManager.getAccountsByType("com.google")) {
                if (account.name.equals(mAccountName)) {
                    selectedAccount = account;
                }
            }

            mAccountManager.invalidateAuthToken("com.google", mToken);

            //Get the Auth token for the selected account
            mAccountManager.getAuthToken(selectedAccount, "oauth2:https://www.googleapis.com/auth/urlshortener", null, this, new AccountManagerCallback<Bundle>() {
                @Override
                public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
                    try {
                        //Get the bundle
                        Bundle bundle = accountManagerFuture.getResult();

                        //Get the intent from bundle
                        Intent intent = (Intent) bundle.get(AccountManager.KEY_INTENT);

                        if (intent != null) {
                            startActivityForResult(intent, RC_AGAIN_TOKEN);
                        } else {
                            mToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                            Log.d("TOKEN", mToken);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, null);
        }
    }

    private boolean checkForAccountPermission() {
        //Check SDK Version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Check for permission
            if (checkSelfPermission(Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                requestPermissions(new String[]{Manifest.permission.GET_ACCOUNTS}, RC_ACCOUNT_PERMISSION);
                return false;
            }
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //Check for result code
        if (requestCode == RC_ACCOUNT_PERMISSION) {
            //Check if permission is granted or not
            if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestToken();
            } else {
                Toast.makeText(this, "Account Permissions required to Sign In", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
