package app.com.thetechnocafe.linkshortner.LinkStats;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import app.com.thetechnocafe.linkshortner.Models.LinkStatsModel.StatsModel;
import app.com.thetechnocafe.linkshortner.R;
import butterknife.ButterKnife;

public class LinkStatsActivity extends AppCompatActivity implements LinkStatsContract.View {

    public static final String EXTRA_SHORT_LINK = "short_link";
    private LinkStatsContract.Presenter mPresenter;

    public static Intent getIntent(Context context, String shortLink) {
        Intent intent = new Intent(context, LinkStatsActivity.class);
        intent.putExtra(EXTRA_SHORT_LINK, shortLink);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_stats);

        ButterKnife.bind(this);

        mPresenter = new LinkStatsPresenter();
        mPresenter.attachView(this);
        mPresenter.loadStatsForUrl(getIntent().getStringExtra(EXTRA_SHORT_LINK));
    }

    @Override
    public Context getAppContext() {
        return getApplicationContext();
    }

    @Override
    public void initViews() {

    }

    @Override
    public void onLoadStats(StatsModel stats) {
        Toast.makeText(this, stats.getAnalytics().getAllTime().getShortUrlClicks(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onResume();
    }
}
