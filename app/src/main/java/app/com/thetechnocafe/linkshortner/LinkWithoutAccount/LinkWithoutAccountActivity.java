package app.com.thetechnocafe.linkshortner.LinkWithoutAccount;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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
            String longLink = mLinkEditText.getText().toString();
            mPresenter.shortenLink(longLink);

            //Toggle Progress Visibility
            toggleProgress(true);
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
        Toast.makeText(getApplicationContext(), longUrl + " -> " + shortUrl, Toast.LENGTH_SHORT).show();
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
}
