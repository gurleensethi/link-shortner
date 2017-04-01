package app.com.thetechnocafe.linkshortner.Services;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import app.com.thetechnocafe.linkshortner.R;
import app.com.thetechnocafe.linkshortner.Services.ClipboardChangeListener.ClipboardChangeListenerContract;
import app.com.thetechnocafe.linkshortner.Services.ClipboardChangeListener.ClipboardChangeListenerPresenter;
import app.com.thetechnocafe.linkshortner.Utilities.Constants;

public class ClipboardChangeListenerService extends Service implements ClipboardChangeListenerContract.View {

    private WindowManager mWindowManager;
    private View mFloatingView;
    private ClipboardChangeListenerContract.Presenter mPresenter;
    private boolean isClipListenerAttached = false;
    private WindowManager.LayoutParams params;
    private String mCurrentLongUrl;
    private AccountManager mAccountManager;
    private ProgressBar mProgressBar;
    private ImageView mShortLinkImageView;
    private ImageView mCancelImageView;
    private ImageView mMoveImageView;

    private ClipboardManager.OnPrimaryClipChangedListener mPrimaryClipChangeListener = () -> {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        //Check if clipboard has primary clip
        if (clipboardManager.hasPrimaryClip()) {
            String primaryClip = clipboardManager.getPrimaryClip().getItemAt(0).getText().toString().trim();

            //Check for SYSTEM_ALERT_WINDOW PERMISSION
            if (canOverlayOverWindow()) {

                //Check if the copied text is a valid long link and not a non-link of (Google shortened link)
                if (!primaryClip.startsWith(Constants.URL_GOO_GL) && (primaryClip.startsWith(Constants.HTTP) || primaryClip.startsWith(Constants.HTTPS) || primaryClip.startsWith(Constants.WWW))) {
                    //Save the current long url
                    mCurrentLongUrl = primaryClip;
                    //Notify the user
                    Toast.makeText(getAppContext(), "Click to shorten link", Toast.LENGTH_SHORT).show();

                    //Show the floating view
                    if (mFloatingView.getParent() != null) {
                        mWindowManager.removeView(mFloatingView);
                    }
                    mWindowManager.addView(mFloatingView, params);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Please grant system permissions to Link Shortener by going to Settings -> Apps -> Draw over other apps", Toast.LENGTH_LONG).show();
            }
        }
    };

    public ClipboardChangeListenerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mAccountManager = AccountManager.get(this);

        mFloatingView = LayoutInflater.from(this).inflate(R.layout.item_floating_view, null);

        //Set up the window manager
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        //Initial position of the floating view
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

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
        if (mFloatingView.getParent() != null) {
            mWindowManager.removeView(mFloatingView);
        }
    }

    @Override
    public Context getAppContext() {
        return getApplicationContext();
    }

    @Override
    public void initViews() {
        //Initialize views for the floating view
        mProgressBar = (ProgressBar) mFloatingView.findViewById(R.id.progress_bar);

        mCancelImageView = (ImageView) mFloatingView.findViewById(R.id.cancel_image_view);
        mCancelImageView.setOnClickListener(view -> {
            //Remove the view from window and reset the parameters
            params.x = 100;
            params.y = 200;
            mWindowManager.removeView(mFloatingView);
        });

        mShortLinkImageView = (ImageView) mFloatingView.findViewById(R.id.shorten_link_image_view);
        mShortLinkImageView.setOnClickListener(view -> {

            //Hide the shortLink image button and show the progress bar
            mProgressBar.setVisibility(View.VISIBLE);
            mShortLinkImageView.setVisibility(View.GONE);

            mPresenter.requestNewTokenAndShortenLink();
        });

        mMoveImageView = (ImageView) mFloatingView.findViewById(R.id.move_image_view);
        mMoveImageView.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        //Save the initial position
                        initialX = params.x;
                        initialY = params.y;

                        //Get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();

                        return true;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void requestTokenAndShortenLink(String oldToken, String accountName) {
        //Temporary account
        Account selectedAccount = null;

        //Retrieve the selected account account
        for (Account account : mAccountManager.getAccountsByType("com.google")) {
            Log.d("TAG", accountName + " == " + account.name);
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
                    mPresenter.saveTokenAndShortenLink(token, mCurrentLongUrl);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, null);
    }

    @Override
    public void onLinkShortened(String shortLink) {
        //Revert the visibility of progress bar and short link image
        mShortLinkImageView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);

        //Reset the position of floating view
        params.x = 100;
        params.y = 200;

        //Hide the floating layout
        if (mFloatingView.getParent() != null) {
            mWindowManager.removeView(mFloatingView);
        }

        //Copy the short link to clipboard and notify user
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("short link", shortLink);
        clipboardManager.setPrimaryClip(clipData);

        Toast.makeText(getApplicationContext(), "Short link copied to clipboard : " + shortLink, Toast.LENGTH_SHORT).show();
    }

    private boolean canOverlayOverWindow() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return true;
        }
        return Settings.canDrawOverlays(getApplicationContext());
    }
}
