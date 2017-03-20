package app.com.thetechnocafe.linkshortner.LinkStats;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import app.com.thetechnocafe.linkshortner.Models.LinkStatsModel.Platform;
import app.com.thetechnocafe.linkshortner.Models.LinkStatsModel.StatsModel;
import app.com.thetechnocafe.linkshortner.R;
import app.com.thetechnocafe.linkshortner.Utilities.Constants;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LinkStatsActivity extends AppCompatActivity implements LinkStatsContract.View {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.platforms_bar_chart)
    BarChart mPlatformBarChart;
    @BindView(R.id.platforms_progress_frame_layout)
    FrameLayout mPlatformsProgressFrameLayout;

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
        //Configure toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_left_white);
    }

    @Override
    public void onLoadStats(StatsModel stats) {
        setUpPlatformBarChart(stats.getAnalytics().getAllTime().getPlatforms());
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //Display the data set in a Bar Chart (MPChart Library)
    private void setUpPlatformBarChart(List<Platform> platforms) {
        //Create bar entries and bar labels
        List<BarEntry> barEntries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        //Iterate over values and add values to barEntries and labels
        for (int i = 0; i < platforms.size(); i++) {
            Platform platform = platforms.get(i);
            barEntries.add(new BarEntry((float) i, Float.parseFloat(platform.getCount())));
            labels.add(platform.getId());
        }

        //Create bar data set
        BarDataSet barDataSet = new BarDataSet(barEntries, "Platforms");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);    //Change bar colors

        //Create bar data from bar data set
        BarData barData = new BarData(barDataSet);

        Description description = new Description();
        description.setText("");

        mPlatformBarChart.setData(barData);
        mPlatformBarChart.getXAxis().setValueFormatter((value, axis) -> labels.get((int) value));   //Labels for X-Axis
        mPlatformBarChart.setDescription(description);  //Set the description

        //Hide all the axis in the chart
        mPlatformBarChart.getXAxis().setDrawGridLines(false);
        mPlatformBarChart.getXAxis().setDrawAxisLine(false);
        mPlatformBarChart.getAxisLeft().setDrawGridLines(false);
        mPlatformBarChart.getAxisLeft().setDrawAxisLine(false);
        mPlatformBarChart.getAxisRight().setEnabled(false);

        mPlatformBarChart.animateXY(Constants.GRAPH_ANIMATION_DURATION, Constants.GRAPH_ANIMATION_DURATION);    //Animate Chart

        //Refresh the bar chart
        mPlatformBarChart.invalidate();

        //Toggle visibility of Progress and Content layout
        mPlatformBarChart.setVisibility(View.VISIBLE);
        mPlatformsProgressFrameLayout.setVisibility(View.GONE);
    }
}
