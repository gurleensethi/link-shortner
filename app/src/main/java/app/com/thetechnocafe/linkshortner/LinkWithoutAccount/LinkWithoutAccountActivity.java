package app.com.thetechnocafe.linkshortner.LinkWithoutAccount;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import app.com.thetechnocafe.linkshortner.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LinkWithoutAccountActivity extends AppCompatActivity implements LinkWithoutAccountContract.View {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.link_edit_text)
    EditText mLinkEditText;
    @BindView(R.id.shorten_link_image_button)
    ImageButton mShortenLinkImageButton;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.link_details_card_view)
    CardView mLinkDetailsCardView;
    @BindView(R.id.original_link_text_view)
    TextView mOriginalLinkTextView;
    @BindView(R.id.shortened_link_text_view)
    TextView mShortenedLinkTextView;
    @BindView(R.id.copy_link_image_view)
    ImageView mCopyImageView;
    @BindView(R.id.share_link_image_view)
    ImageView mShareImageView;

    private static final String CLIPBOARD_SHORT_LINK_LABEL = "shortened link";

    private LinkWithoutAccountContract.Presenter mPresenter;

    //Return the Intent required to launch this Activity
    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, LinkWithoutAccountActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_without_account);

        ButterKnife.bind(this);

        mPresenter = new LinkWithoutAccountPresenter();
        mPresenter.attachView(this);
    }

    @Override
    public Context getAppContext() {
        return getApplicationContext();
    }

    @Override
    public void initViews() {
        //Configure Toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_left_white);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mShortenLinkImageButton.setOnClickListener(view -> {
            //Hide the card layout with animation
            Animation centerToLeftAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.center_to_left);
            mLinkDetailsCardView.startAnimation(centerToLeftAnimation);
            mLinkDetailsCardView.setVisibility(View.GONE);

            String longLink = mLinkEditText.getText().toString();
            mPresenter.shortenLink(longLink);

            //Toggle Progress Visibility
            toggleProgress(true);

            //Hide the keyboard
            hideKeyboard();
        });

        //Copy the shortened link to clipboard when clicked on copy image
        mCopyImageView.setOnClickListener(view -> {
            //Get the shortened text from text view
            String shortenedLink = mShortenedLinkTextView.getText().toString();

            //Get the clipboard manager service
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

            //Create clip data from text
            ClipData clipData = ClipData.newPlainText(CLIPBOARD_SHORT_LINK_LABEL, shortenedLink);

            clipboardManager.setPrimaryClip(clipData);

            Toast.makeText(getApplicationContext(), R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show();
        });

        //Send a text intent when shared image clicked
        mShareImageView.setOnClickListener(view -> {
            //Create intent and set action
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);

            //Get the text from text view and set it
            intent.putExtra(Intent.EXTRA_TEXT, mShortenedLinkTextView.getText().toString());
            intent.setType("text/plain");
            startActivity(intent);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onLinkShortened(String shortUrl, String longUrl) {
        //Set the links to text views
        mShortenedLinkTextView.setText(shortUrl);
        mOriginalLinkTextView.setText(longUrl);

        //Show the details card view and animate its entry
        Animation rightToCenterAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_to_center);
        mLinkDetailsCardView.startAnimation(rightToCenterAnimation);
        mLinkDetailsCardView.setVisibility(View.VISIBLE);

        toggleProgress(false);
    }

    @Override
    public void onLinkShortenedError(String error) {
        Toast.makeText(getApplicationContext(), "Error occurred while shortening link.", Toast.LENGTH_SHORT).show();
        toggleProgress(false);
    }

    //Toggle progress bar visibility
    private void toggleProgress(boolean isLoading) {
        if (isLoading) {
            mProgressBar.setVisibility(View.VISIBLE);
            mShortenLinkImageButton.setVisibility(View.GONE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mShortenLinkImageButton.setVisibility(View.VISIBLE);
        }
    }

    //Hide the input keyboard
    private void hideKeyboard() {
        //Get the current view that has the focus
        View view = getCurrentFocus();

        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
