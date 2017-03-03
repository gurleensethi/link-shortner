package app.com.thetechnocafe.linkshortner.Home;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.List;

import app.com.thetechnocafe.linkshortner.Adapters.LinksRecyclerAdapter;
import app.com.thetechnocafe.linkshortner.Models.UrlListModels.ShortLink;
import app.com.thetechnocafe.linkshortner.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements HomeContract.View {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.links_recycler_view)
    RecyclerView mLinksRecyclerView;

    private HomeContract.Presenter mPresenter;
    private LinksRecyclerAdapter mLinksRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.home);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);

        //Configure recycler view
        mLinksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mLinksRecyclerView.setNestedScrollingEnabled(false);

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
    public void onShortLinksReceived(List<ShortLink> shortLinks) {
        setUpOrRefreshRecyclerView(shortLinks);
    }

    private void setUpOrRefreshRecyclerView(List<ShortLink> shortLinks) {
        if (mLinksRecyclerAdapter == null) {
            mLinksRecyclerAdapter = new LinksRecyclerAdapter(this, shortLinks);
            mLinksRecyclerView.setAdapter(mLinksRecyclerAdapter);
        } else {
            mLinksRecyclerAdapter.notifyDataSetChanged();
        }
    }
}
