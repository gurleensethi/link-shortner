package app.com.thetechnocafe.linkshortner.Adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import app.com.thetechnocafe.linkshortner.Models.UrlListModels.Analytics;
import app.com.thetechnocafe.linkshortner.Models.UrlListModels.ShortLink;
import app.com.thetechnocafe.linkshortner.R;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by gurleensethi on 09/03/17.
 */

public class AllLinksRecyclerAdapter extends RecyclerView.Adapter<AllLinksRecyclerAdapter.AllLinksViewHolder> {

    private Context mContext;
    private List<ShortLink> mShortLinkList;
    private OnLinkClickListener mListener;
    private static final String CLIPBOARD_SHORT_LINK_LABEL = "shortened link";

    //Interface for callbacks
    public interface OnLinkClickListener {
        void onClick(ShortLink shortLink);
    }

    public AllLinksRecyclerAdapter(Context context, List<ShortLink> shortLinks) {
        mContext = context;
        mShortLinkList = shortLinks;
    }

    //View holder
    class AllLinksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.original_link_text_view)
        TextView mOriginalLinkTextView;
        @BindView(R.id.shortened_link_text_view)
        TextView mShortenedLinkTextView;
        @BindView(R.id.total_clicks_text_view)
        TextView mTotalClicksTextView;
        @BindView(R.id.copy_link_image_view)
        ImageView mCopyImageView;
        @BindView(R.id.share_link_image_view)
        ImageView mShareImageView;
        @BindView(R.id.open_details_image_view)
        ImageView mOpenDetailsImageView;
        private int mPosition;

        public AllLinksViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ButterKnife.bind(this, itemView);

            //Set on click listeners for action buttons
            mCopyImageView.setOnClickListener(view -> {
                //Get the clipboard manager service
                ClipboardManager clipboardManager = (ClipboardManager) mContext.getSystemService(CLIPBOARD_SERVICE);

                //Create clip data from text
                ClipData clipData = ClipData.newPlainText(CLIPBOARD_SHORT_LINK_LABEL, mShortLinkList.get(mPosition).getId());

                clipboardManager.setPrimaryClip(clipData);

                Toast.makeText(mContext, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show();
            });

            mShareImageView.setOnClickListener(view -> {
                //Create intent and set action
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);

                //Get the text from text view and set it
                intent.putExtra(Intent.EXTRA_TEXT, mShortLinkList.get(mPosition).getId());
                intent.setType("text/plain");
                mContext.startActivity(intent);
            });
        }

        void bindData(int position) {
            mPosition = position;

            //Get the link model
            ShortLink shortLink = mShortLinkList.get(mPosition);

            //Set the values to views
            mOriginalLinkTextView.setText(shortLink.getLongUrl());
            mShortenedLinkTextView.setText(shortLink.getId());

            Analytics analytics = shortLink.getAnalytics();
            if (analytics != null) {
                mTotalClicksTextView.setText(analytics.getAllTime().getShortUrlClicks());
            } else {
                mTotalClicksTextView.setText("Not found");
            }
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onClick(mShortLinkList.get(mPosition));
            }
        }
    }

    @Override
    public AllLinksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recycler_all_links, parent, false);
        return new AllLinksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AllLinksViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return mShortLinkList.size();
    }

    public void setOnLinkClickListener(OnLinkClickListener listener) {
        mListener = listener;
    }
}
