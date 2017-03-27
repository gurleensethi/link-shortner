package app.com.thetechnocafe.linkshortner.Services;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import app.com.thetechnocafe.linkshortner.Services.ClipboardChangeListener.ClipboardChangeListenerContract;
import app.com.thetechnocafe.linkshortner.Services.ClipboardChangeListener.ClipboardChangeListenerPresenter;
import app.com.thetechnocafe.linkshortner.Utilities.Constants;

public class ClipboardChangeListenerService extends Service implements ClipboardChangeListenerContract.View {

    private ClipboardChangeListenerContract.Presenter mPresenter;
    private boolean isClipListenerAttached = false;

    private ClipboardManager.OnPrimaryClipChangedListener mPrimaryClipChangeListener = () -> {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        //Check if clipboard has primary clip
        if (clipboardManager.hasPrimaryClip()) {
            String primaryClip = clipboardManager.getPrimaryClip().getItemAt(0).getText().toString().trim();

            //Check if the copied text is a valid long link and not a non-link of (Google shortened link)
            if (!primaryClip.startsWith(Constants.URL_GOO_GL) && (primaryClip.startsWith(Constants.HTTP) || primaryClip.startsWith(Constants.HTTPS) || primaryClip.startsWith(Constants.WWW))) {
                Toast.makeText(getAppContext(), primaryClip, Toast.LENGTH_SHORT).show();
            }
        }
    };

    public ClipboardChangeListenerService() {
        mPresenter = new ClipboardChangeListenerPresenter();
        mPresenter.attachView(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Attach listener to clipboard manager to listen for copy changes
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        if (!isClipListenerAttached) {
            clipboardManager.addPrimaryClipChangedListener(mPrimaryClipChangeListener);
            isClipListenerAttached = true;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public Context getAppContext() {
        return getApplicationContext();
    }

    @Override
    public void initViews() {
        //NO VIEWS FOR SERVICE
    }

    //Request the OAuth Token
    private void requestTokenAndShortenLink(String oldToken, String accountName, String longUrl) {
        AccountManager mAccountManager = AccountManager.get(this);
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
        mAccountManager.getAuthToken(selectedAccount, "oauth2:https://www.googleapis.com/auth/urlshortener", null, null, accountManagerFuture -> {
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
                    //mPresenter.saveNewToken(token);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, null);
    }
}
