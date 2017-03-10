package app.com.thetechnocafe.linkshortner.AllLinks;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.List;

import app.com.thetechnocafe.linkshortner.Adapters.AllLinksRecyclerAdapter;
import app.com.thetechnocafe.linkshortner.Models.UrlListModels.ShortLink;
import app.com.thetechnocafe.linkshortner.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AllLinksActivity extends AppCompatActivity implements AllLinksContract.View {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.short_links_recycler_view)
    RecyclerView mShortLinksRecyclerView;

    private AllLinksContract.Presenter mPresenter;
    private AllLinksRecyclerAdapter mAllLinksRecyclerAdapter;

    //Get intent method
    public static Intent getIntent(Context context) {
        return new Intent(context, AllLinksActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_links);

        ButterKnife.bind(this);

        mPresenter = new AllLinksPresenter();
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_left_white);

        //Configure recycler view
        mShortLinksRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    @Override
    public void onShortLinksLoaded(List<ShortLink> shortLinks) {
        setUpOrRefreshRecyclerView(shortLinks);
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

    private void setUpOrRefreshRecyclerView(List<ShortLink> shortLinks) {
        if (mAllLinksRecyclerAdapter == null) {
            mAllLinksRecyclerAdapter = new AllLinksRecyclerAdapter(this, shortLinks);
            mShortLinksRecyclerView.setAdapter(mAllLinksRecyclerAdapter);
        } else {
            mAllLinksRecyclerAdapter.notifyDataSetChanged();
        }
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
}
