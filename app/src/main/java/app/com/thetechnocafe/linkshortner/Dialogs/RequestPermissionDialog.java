package app.com.thetechnocafe.linkshortner.Dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import app.com.thetechnocafe.linkshortner.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gurleensethi on 01/04/17.
 */

public class RequestPermissionDialog extends android.support.v4.app.DialogFragment {

    //Interface for callbacks
    public interface OnActionListener {
        void onOkClicked();
    }

    //Instance method
    public static RequestPermissionDialog getInstance() {
        return new RequestPermissionDialog();
    }

    @BindView(R.id.ok_button)
    Button mOkButton;

    private OnActionListener mActionListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_request_permission, container, false);

        ButterKnife.bind(this, view);

        setUpEventListeners();
        return view;
    }

    private void setUpEventListeners() {
        mOkButton.setOnClickListener(view -> {
            if (mActionListener != null) {
                mActionListener.onOkClicked();
            }

            //Dismiss the dialog
            getDialog().dismiss();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public void addOnActionListener(OnActionListener listener) {
        mActionListener = listener;
    }
}
