package app.com.thetechnocafe.linkshortner.Home;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.transition.TransitionManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import app.com.thetechnocafe.linkshortner.Adapters.LinksRecyclerAdapter;
import app.com.thetechnocafe.linkshortner.AllLinks.AllLinksActivity;
import app.com.thetechnocafe.linkshortner.Models.UrlListModels.ShortLink;
import app.com.thetechnocafe.linkshortner.R;
import app.com.thetechnocafe.linkshortner.Services.ClipboardChangeListenerService;
import app.com.thetechnocafe.linkshortner.SignIn.SignInActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements HomeContract.View {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.links_recycler_view)
    RecyclerView mLinksRecyclerView;
    @BindView(R.id.no_shortened_links_text_view)
    TextView mNoShortenedLinksTextView;
    @BindView(R.id.view_all_links_linear_layout)
    LinearLayout mViewAllLinksLinearLayout;
    @BindView(R.id.total_clicks_text_view)
    TextView mTotalClicksTextView;
    @BindView(R.id.total_links_shortened_text_view)
    TextView mTotalLinksShortenedTextView;
    @BindView(R.id.linear_layout)
    LinearLayout mLinearLayout;
    @BindView(R.id.edit_text_card_view)
    CardView mEditTextCardView;
    @BindView(R.id.link_edit_text)
    EditText mLinkEditText;
    @BindView(R.id.shorten_link_image_button)
    ImageButton mShortenLinkImageButton;
    @BindView(R.id.short_link_progress_bar)
    ProgressBar mShortLinkProgressBar;
    @BindView(R.id.link_details_card_view)
    CardView mLinkDetailsCardView;
    @BindView(R.id.original_link_text_view)
    TextView mOriginalLinkTextView;
    @BindView(R.id.shortened_link_text_view)
    TextView mShortenedLinkTextView;
    @BindView(R.id.copy_link_image_view)
    ImageView mCopyLinkImageView;
    @BindView(R.id.share_link_image_view)
    ImageView mShareLinkImageView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private HomeContract.Presenter mPresenter;
    private LinksRecyclerAdapter mLinksRecyclerAdapter;
    private AccountManager mAccountManager;
    private static final String CLIPBOARD_SHORT_LINK_LABEL = "shortened link";

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

        //Configure recycler view
        mLinksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mLinksRecyclerView.setNestedScrollingEnabled(false);

        mViewAllLinksLinearLayout.setOnClickListener(view -> {
            startActivity(AllLinksActivity.getIntent(getApplicationContext()));
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        });

        mShortenLinkImageButton.setOnClickListener(view -> {
            //Check if the link is not empty
            String longUrl = mLinkEditText.getText().toString();

            if (longUrl.length() == 0) {
                mLinkEditText.requestFocus();
                mLinkEditText.setError("Url cannot be empty");
                return;
            }

            //Hide the card layout
            TransitionManager.beginDelayedTransition(mLinearLayout);
            mLinkDetailsCardView.setVisibility(View.GONE);

            toggleProgress(true);
            hideSoftKeyboard();

            mLinkEditText.setEnabled(false);

            mPresenter.shortenUrl(longUrl);
        });

        //Configure Swipe refresh layout
        mSwipeRefreshLayout.setColorSchemeResources(R.color.md_red_500, R.color.md_blue_500, R.color.md_green_500, R.color.md_yellow_500);
        mSwipeRefreshLayout.setOnRefreshListener(() -> mPresenter.reloadLinks());

        //Start the clipboard change listener service
        Intent intent = new Intent(this, ClipboardChangeListenerService.class);
        startService(intent);
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

            //Hide swife refresh layout
            mSwipeRefreshLayout.setRefreshing(false);

            //Send data to recycler view
            setUpOrRefreshRecyclerView(shortLinks);
        } else {
            mLinksRecyclerView.setVisibility(View.GONE);
            mNoShortenedLinksTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setTotalClicks(int count) {
        mTotalClicksTextView.setText(String.valueOf(count));
    }

    @Override
    public void setTotalShortenedLinks(int count) {
        mTotalLinksShortenedTextView.setText(String.valueOf(count));
    }

    @Override
    public void onLinkShortened(String shortUrl, String longUrl) {
        mLinkEditText.setEnabled(true);

        mOriginalLinkTextView.setText(longUrl);
        mShortenedLinkTextView.setText(shortUrl);

        mCopyLinkImageView.setOnClickListener(view -> {
            //Get the shortened text from text view
            String shortenedLink = mShortenedLinkTextView.getText().toString();

            //Get the clipboard manager service
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

            //Create clip data from text
            ClipData clipData = ClipData.newPlainText(CLIPBOARD_SHORT_LINK_LABEL, shortenedLink);

            clipboardManager.setPrimaryClip(clipData);

            Toast.makeText(getApplicationContext(), R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show();
        });

        mShareLinkImageView.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, shortUrl);
            intent.setType("text/plain");
            startActivity(intent);
        });

        //Animate
        TransitionManager.beginDelayedTransition(mLinearLayout);
        mLinkDetailsCardView.setVisibility(View.VISIBLE);

        toggleProgress(false);
    }

    @Override
    public void onLinkShortenError() {
        mLinkEditText.setEnabled(false);

        toggleProgress(false);
        Snackbar.make(mLinearLayout, "Unable to shorten link", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void startRefreshing() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void stopRefreshing() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void setUpOrRefreshRecyclerView(List<ShortLink> shortLinks) {
        if (mLinksRecyclerAdapter == null) {
            mLinksRecyclerAdapter = new LinksRecyclerAdapter(this, shortLinks);
            mLinksRecyclerView.setAdapter(mLinksRecyclerAdapter);
        } else {
            mLinksRecyclerAdapter.updateList(shortLinks);
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

                    Log.d("HomeActivity", token);

                    //Save the account and token
                    mPresenter.saveNewToken(token);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sign_out: {
                //Stop the service
                Intent serviceIntent = new Intent(this, ClipboardChangeListenerService.class);
                stopService(serviceIntent);

                mPresenter.signOut();

                //Start the sign in activity
                Intent intent = new Intent(this, SignInActivity.class);
                startActivity(intent);

                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleProgress(boolean isLoading) {
        if (isLoading) {
            mLinkEditText.setEnabled(false);
            mShortLinkProgressBar.setVisibility(View.VISIBLE);
            mShortenLinkImageButton.setVisibility(View.GONE);
        } else {
            mLinkEditText.setEnabled(true);
            mShortLinkProgressBar.setVisibility(View.GONE);
            mShortenLinkImageButton.setVisibility(View.VISIBLE);
        }
    }

    private void hideSoftKeyboard() {
        View view = getCurrentFocus();

        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
