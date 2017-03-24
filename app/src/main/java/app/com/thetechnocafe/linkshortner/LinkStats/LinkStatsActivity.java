package app.com.thetechnocafe.linkshortner.LinkStats;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.transition.TransitionManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import app.com.thetechnocafe.linkshortner.Models.LinkStatsModel.Analytics;
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
    @BindView(R.id.original_link_text_view)
    TextView mOriginalLinkTextView;
    @BindView(R.id.short_link_text_view)
    TextView mShortLinkTextView;
    @BindView(R.id.time_ago_text_view)
    TextView mTimeAgoTextView;
    @BindView(R.id.general_stats_relative_layout)
    RelativeLayout mGeneralStatsRelativeLayout;
    @BindView(R.id.linear_layout)
    LinearLayout mLinearLayout;
    @BindView(R.id.total_short_url_clicks_text_view)
    TextView mTotalShortUrlClicksTextView;
    @BindView(R.id.total_long_url_clicks_text_view)
    TextView mTotalLongUrlClicksTextView;
    @BindView(R.id.select_time_spinner)
    Spinner mSelectTimeSpinner;

    private static final String[] STATS_TIME_OPTIONS = {"All Time", "Month", "Week", "Day", "TwoHours"};
    public static final String EXTRA_SHORT_LINK = "short_link";
    private LinkStatsContract.Presenter mPresenter;
    private StatsModel STATS_MODEL;

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
        STATS_MODEL = stats;

        //Set the general stats
        mTotalShortUrlClicksTextView.setText(String.valueOf(stats.getAnalytics().getAllTime().getShortUrlClicks()));
        mTotalLongUrlClicksTextView.setText(String.valueOf(stats.getAnalytics().getAllTime().getLongUrlClicks()));
        mShortLinkTextView.setText(stats.getId());

        //Set original link
        SpannableString originalLinkSpannableString = new SpannableString(" " + stats.getLongUrl());
        originalLinkSpannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.md_blue_500)), 0, originalLinkSpannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mOriginalLinkTextView.append(originalLinkSpannableString);

        //Convert date to string
        //Convert date to long
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        long time = 0l;
        try {
            time = simpleDateFormat.parse(stats.getCreated().split("\\.")[0]).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        long timeDifference = new Date().getTime() - time;
        String timeString = "Created ";
        if (TimeUnit.MILLISECONDS.toDays(timeDifference) > 0) {
            timeString += TimeUnit.MILLISECONDS.toDays(timeDifference) + " days ago";
        } else if (TimeUnit.MILLISECONDS.toMinutes(timeDifference) > 0) {
            timeString += TimeUnit.MILLISECONDS.toMinutes(timeDifference) + " minutes ago";
        } else if (TimeUnit.MILLISECONDS.toSeconds(timeDifference) > 0) {
            timeString += TimeUnit.MILLISECONDS.toSeconds(timeDifference) + " seconds ago";
        }
        mTimeAgoTextView.setText(timeString);

        //Animate and show layout
        TransitionManager.beginDelayedTransition(mGeneralStatsRelativeLayout);
        mGeneralStatsRelativeLayout.setVisibility(View.VISIBLE);

        //Configure select time spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, STATS_TIME_OPTIONS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSelectTimeSpinner.setAdapter(adapter);
        mSelectTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Analytics analytics = stats.getAnalytics();
                switch (position) {
                    case 0: {
                        setUpPlatformBarChart(analytics.getAllTime().getPlatforms());
                        setUpBrowsersBarChart(analytics.getAllTime().getBrowsers());
                        setUpReferrersPieChart(analytics.getAllTime().getReferrers());
                        setUpCountryBarChart(analytics.getAllTime().getCountries());
                        break;
                    }
                    case 1: {
                        setUpPlatformBarChart(analytics.getMonth().getPlatforms());
                        setUpBrowsersBarChart(analytics.getMonth().getBrowsers());
                        setUpReferrersPieChart(analytics.getMonth().getReferrers());
                        setUpCountryBarChart(analytics.getMonth().getCountries());
                        break;
                    }
                    case 2: {
                        setUpPlatformBarChart(analytics.getWeek().getPlatforms());
                        setUpBrowsersBarChart(analytics.getWeek().getBrowsers());
                        setUpReferrersPieChart(analytics.getWeek().getReferrers());
                        setUpCountryBarChart(analytics.getWeek().getCountries());
                        break;
                    }
                    case 3: {
                        setUpPlatformBarChart(analytics.getDay().getPlatforms());
                        setUpBrowsersBarChart(analytics.getDay().getBrowsers());
                        setUpReferrersPieChart(analytics.getDay().getReferrers());
                        setUpCountryBarChart(analytics.getDay().getCountries());
                        break;
                    }
                    case 4: {
                        setUpPlatformBarChart(analytics.getTwoHours().getPlatforms());
                        setUpBrowsersBarChart(analytics.getTwoHours().getBrowsers());
                        setUpReferrersPieChart(analytics.getTwoHours().getReferrers());
                        setUpCountryBarChart(analytics.getTwoHours().getCountries());
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Set up the charts initially for All Time stats
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
                overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
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
            mPlatformHorizontalBarChart.setNoDataTextColor(ContextCompat.getColor(this, R.color.md_blue_500));   //Change the no data available color

            //Hide all the axis in the chart
            mPlatformHorizontalBarChart.getXAxis().setDrawGridLines(false);
            mPlatformHorizontalBarChart.getXAxis().setDrawAxisLine(false);
            mPlatformHorizontalBarChart.getAxisLeft().setDrawGridLines(false);
            mPlatformHorizontalBarChart.getAxisLeft().setDrawAxisLine(false);
            mPlatformHorizontalBarChart.getAxisRight().setEnabled(false);

            mPlatformHorizontalBarChart.animateXY(Constants.GRAPH_ANIMATION_DURATION, Constants.GRAPH_ANIMATION_DURATION);    //Animate Chart

        } else {
            mPlatformHorizontalBarChart.setData(null);
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
            mBrowsersHorizontalBarChart.setNoDataTextColor(ContextCompat.getColor(this, R.color.md_blue_500));   //Change the no data available color

            //Hide all the axis in the chart
            mBrowsersHorizontalBarChart.getXAxis().setDrawGridLines(false);
            mBrowsersHorizontalBarChart.getXAxis().setDrawAxisLine(false);
            mBrowsersHorizontalBarChart.getAxisLeft().setDrawGridLines(false);
            mBrowsersHorizontalBarChart.getAxisLeft().setDrawAxisLine(false);
            mBrowsersHorizontalBarChart.getAxisRight().setEnabled(false);

            mBrowsersHorizontalBarChart.animateXY(Constants.GRAPH_ANIMATION_DURATION, Constants.GRAPH_ANIMATION_DURATION);    //Animate Chart

        } else {
            mBrowsersHorizontalBarChart.setData(null);
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
        if (referrers != null) {

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
            mReferrersPieChart.setNoDataTextColor(ContextCompat.getColor(this, R.color.md_blue_500));   //Change the no data available color

            Description description = new Description();
            description.setText("Referrers");
            mReferrersPieChart.setDescription(description);     //Change the description
        } else {
            mReferrersPieChart.setData(null);
        }

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
            mCountryHorizontalBarChart.setNoDataTextColor(ContextCompat.getColor(this, R.color.md_blue_500));   //Change the no data available color

            //Hide all the axis in the chart
            mCountryHorizontalBarChart.getXAxis().setDrawGridLines(false);
            mCountryHorizontalBarChart.getXAxis().setDrawAxisLine(false);
            mCountryHorizontalBarChart.getAxisLeft().setDrawGridLines(false);
            mCountryHorizontalBarChart.getAxisLeft().setDrawAxisLine(false);
            mCountryHorizontalBarChart.getAxisRight().setEnabled(false);

            mCountryHorizontalBarChart.animateXY(Constants.GRAPH_ANIMATION_DURATION, Constants.GRAPH_ANIMATION_DURATION);    //Animate Chart

        } else {
            mCountryHorizontalBarChart.setData(null);
        }

        //Refresh the bar chart
        mCountryHorizontalBarChart.invalidate();

        //Toggle visibility of Progress and Content layout
        mCountryHorizontalBarChart.setVisibility(View.VISIBLE);
        mCountryProgressFrameLayout.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }
}
