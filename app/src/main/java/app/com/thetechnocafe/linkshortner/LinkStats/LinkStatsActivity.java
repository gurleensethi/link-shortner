package app.com.thetechnocafe.linkshortner.LinkStats;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import app.com.thetechnocafe.linkshortner.Models.LinkStatsModel.Browser;
import app.com.thetechnocafe.linkshortner.Models.LinkStatsModel.Country;
import app.com.thetechnocafe.linkshortner.Models.LinkStatsModel.Platform;
import app.com.thetechnocafe.linkshortner.Models.LinkStatsModel.Referrer;
import app.com.thetechnocafe.linkshortner.Models.LinkStatsModel.StatsModel;
import app.com.thetechnocafe.linkshortner.R;
import app.com.thetechnocafe.linkshortner.Utilities.Constants;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LinkStatsActivity extends AppCompatActivity implements LinkStatsContract.View {

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.platforms_horizontal_bar_chart)
    HorizontalBarChart mPlatformHorizontalBarChart;
    @BindView(R.id.platforms_progress_frame_layout)
    FrameLayout mPlatformsProgressFrameLayout;
    @BindView(R.id.browsers_horizontal_bar_chart)
    HorizontalBarChart mBrowsersHorizontalBarChart;
    @BindView(R.id.browsers_progress_frame_layout)
    FrameLayout mBrowsersProgressFrameLayout;
    @BindView(R.id.platforms_progress_bar)
    ProgressBar mPlatformsProgressBar;
    @BindView(R.id.browsers_progress_bar)
    ProgressBar mBrowsersProgressBar;
    @BindView(R.id.platforms_error_image_view)
    ImageView mPlatformsErrorImageView;
    @BindView(R.id.browsers_error_image_view)
    ImageView mBrowsersErrorImageView;
    @BindView(R.id.referrers_pie_chart)
    PieChart mReferrersPieChart;
    @BindView(R.id.referrers_error_image_view)
    ImageView mReferrersErrorImageView;
    @BindView(R.id.referrers_progress_bar)
    ProgressBar mReferrersProgressBar;
    @BindView(R.id.referrers_progress_frame_layout)
    FrameLayout mReferrersProgressFrameLayout;
    @BindView(R.id.country_horizontal_bar_chart)
    HorizontalBarChart mCountryHorizontalBarChart;
    @BindView(R.id.country_error_image_view)
    ImageView mCountryErrorImageView;
    @BindView(R.id.country_progress_frame_layout)
    FrameLayout mCountryProgressFrameLayout;
    @BindView(R.id.country_progress_bar)
    ProgressBar mCountryProgressBar;

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
        setUpBrowsersBarChart(stats.getAnalytics().getAllTime().getBrowsers());
        setUpReferrersPieChart(stats.getAnalytics().getAllTime().getReferrers());
        setUpCountryBarChart(stats.getAnalytics().getAllTime().getCountries());
    }

    @Override
    public void onNetworkError() {
        Snackbar.make(mCoordinatorLayout, "Error while connecting to GoogleAPI", Snackbar.LENGTH_LONG).show();

        mPlatformsProgressBar.setVisibility(View.GONE);
        mBrowsersProgressBar.setVisibility(View.GONE);
        mReferrersProgressBar.setVisibility(View.GONE);
        mCountryProgressBar.setVisibility(View.GONE);
        mPlatformsErrorImageView.setVisibility(View.VISIBLE);
        mBrowsersErrorImageView.setVisibility(View.VISIBLE);
        mReferrersErrorImageView.setVisibility(View.VISIBLE);
        mCountryErrorImageView.setVisibility(View.VISIBLE);
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
        //Check if data is available
        if (platforms != null) {

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
            barDataSet.setColor(ContextCompat.getColor(this, R.color.md_blue_500));    //Change bar colors

            //Create bar data from bar data set
            BarData barData = new BarData(barDataSet);

            Description description = new Description();
            description.setText("");

            mPlatformHorizontalBarChart.setData(barData);
            mPlatformHorizontalBarChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
            mPlatformHorizontalBarChart.setDescription(description);  //Set the description
            mPlatformHorizontalBarChart.getXAxis().setGranularityEnabled(true);
            mPlatformHorizontalBarChart.getXAxis().setGranularity(1f);

            //Hide all the axis in the chart
            mPlatformHorizontalBarChart.getXAxis().setDrawGridLines(false);
            mPlatformHorizontalBarChart.getXAxis().setDrawAxisLine(false);
            mPlatformHorizontalBarChart.getAxisLeft().setDrawGridLines(false);
            mPlatformHorizontalBarChart.getAxisLeft().setDrawAxisLine(false);
            mPlatformHorizontalBarChart.getAxisRight().setEnabled(false);

            mPlatformHorizontalBarChart.animateXY(Constants.GRAPH_ANIMATION_DURATION, Constants.GRAPH_ANIMATION_DURATION);    //Animate Chart

        }

        //Refresh the bar chart
        mPlatformHorizontalBarChart.invalidate();

        //Toggle visibility of Progress and Content layout
        mPlatformHorizontalBarChart.setVisibility(View.VISIBLE);
        mPlatformsProgressFrameLayout.setVisibility(View.GONE);
    }

    //Display the data set in a Bar Chart (MPChart Library)
    private void setUpBrowsersBarChart(List<Browser> browsers) {
        //Check if data is available
        if (browsers != null) {

            //Create bar entries and bar labels
            List<BarEntry> barEntries = new ArrayList<>();
            List<String> labels = new ArrayList<>();

            //Iterate over values and add values to barEntries and labels
            for (int i = 0; i < browsers.size(); i++) {
                Browser browser = browsers.get(i);
                barEntries.add(new BarEntry((float) i, Float.parseFloat(browser.getCount())));
                labels.add(browser.getId());
            }

            //Create bar data set
            BarDataSet barDataSet = new BarDataSet(barEntries, "Browsers");
            barDataSet.setColor(ContextCompat.getColor(this, R.color.md_blue_500));    //Change bar colors

            //Create bar data from bar data set
            BarData barData = new BarData(barDataSet);

            Description description = new Description();
            description.setText("");

            mBrowsersHorizontalBarChart.setData(barData);
            mBrowsersHorizontalBarChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
            mBrowsersHorizontalBarChart.setDescription(description);  //Set the description
            mBrowsersHorizontalBarChart.getXAxis().setGranularityEnabled(true);
            mBrowsersHorizontalBarChart.getXAxis().setGranularity(1f);

            //Hide all the axis in the chart
            mBrowsersHorizontalBarChart.getXAxis().setDrawGridLines(false);
            mBrowsersHorizontalBarChart.getXAxis().setDrawAxisLine(false);
            mBrowsersHorizontalBarChart.getAxisLeft().setDrawGridLines(false);
            mBrowsersHorizontalBarChart.getAxisLeft().setDrawAxisLine(false);
            mBrowsersHorizontalBarChart.getAxisRight().setEnabled(false);

            mBrowsersHorizontalBarChart.animateXY(Constants.GRAPH_ANIMATION_DURATION, Constants.GRAPH_ANIMATION_DURATION);    //Animate Chart

        }

        //Refresh the bar chart
        mBrowsersHorizontalBarChart.invalidate();

        //Toggle visibility of Progress and Content layout
        mBrowsersHorizontalBarChart.setVisibility(View.VISIBLE);
        mBrowsersProgressFrameLayout.setVisibility(View.GONE);
    }

    //Display the data set in a Pie Chart (MPChart Library)
    private void setUpReferrersPieChart(List<Referrer> referrers) {
        //Check if data is available
        if (referrers == null) {
            return;
        }

        List<PieEntry> pieEntries = new ArrayList<>();

        for (int i = 0; i < referrers.size(); i++) {
            Referrer referrer = referrers.get(i);

            pieEntries.add(new PieEntry(Float.parseFloat(referrer.getCount()), referrer.getId()));
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter());

        mReferrersPieChart.setEntryLabelTextSize(0f);
        mReferrersPieChart.setData(pieData);

        Description description = new Description();
        description.setText("Referrers");
        mReferrersPieChart.setDescription(description);     //Change the description

        //Refresh the pie chart
        mReferrersPieChart.invalidate();

        mReferrersPieChart.setVisibility(View.VISIBLE);
        mReferrersProgressFrameLayout.setVisibility(View.GONE);
    }

    //Display the data set in a Horizontal Bar Chart(MP Chart Library)
    private void setUpCountryBarChart(List<Country> countries) {
        //Check if data is available
        if (countries != null) {

            //Create bar entries and bar labels
            List<BarEntry> barEntries = new ArrayList<>();
            List<String> labels = new ArrayList<>();

            //Iterate over values and add values to barEntries and labels
            for (int i = 0; i < countries.size(); i++) {
                Country country = countries.get(i);
                barEntries.add(new BarEntry((float) i, Float.parseFloat(country.getCount())));
                labels.add(country.getId());
            }

            //Create bar data set
            BarDataSet barDataSet = new BarDataSet(barEntries, "Country");
            barDataSet.setColor(ContextCompat.getColor(this, R.color.md_blue_500));    //Change bar colors

            //Create bar data from bar data set
            BarData barData = new BarData(barDataSet);

            Description description = new Description();
            description.setText("");

            mCountryHorizontalBarChart.setData(barData);
            mCountryHorizontalBarChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
            mCountryHorizontalBarChart.setDescription(description);  //Set the description
            mCountryHorizontalBarChart.getXAxis().setGranularityEnabled(true);
            mCountryHorizontalBarChart.getXAxis().setGranularity(1f);

            //Hide all the axis in the chart
            mCountryHorizontalBarChart.getXAxis().setDrawGridLines(false);
            mCountryHorizontalBarChart.getXAxis().setDrawAxisLine(false);
            mCountryHorizontalBarChart.getAxisLeft().setDrawGridLines(false);
            mCountryHorizontalBarChart.getAxisLeft().setDrawAxisLine(false);
            mCountryHorizontalBarChart.getAxisRight().setEnabled(false);

            mCountryHorizontalBarChart.animateXY(Constants.GRAPH_ANIMATION_DURATION, Constants.GRAPH_ANIMATION_DURATION);    //Animate Chart

        }

        //Refresh the bar chart
        mCountryHorizontalBarChart.invalidate();

        //Toggle visibility of Progress and Content layout
        mCountryHorizontalBarChart.setVisibility(View.VISIBLE);
        mCountryProgressFrameLayout.setVisibility(View.GONE);
    }
}
