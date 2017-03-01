package app.com.thetechnocafe.linkshortner.Home;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import app.com.thetechnocafe.linkshortner.R;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);
    }
}
