package app.com.thetechnocafe.linkshortner.Services.ClipboardChangeListener;

/**
 * Created by gurleensethi on 27/03/17.
 */

public class ClipboardChangeListenerPresenter implements ClipboardChangeListenerContract.Presenter {

    private ClipboardChangeListenerContract.View mMainView;

    @Override
    public void attachView(ClipboardChangeListenerContract.View view) {
        mMainView = view;
    }

    @Override
    public void detachView() {
        mMainView = null;
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }
}
