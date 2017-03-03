package app.com.thetechnocafe.linkshortner.Adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import app.com.thetechnocafe.linkshortner.Models.UrlListModels.ShortLink;
import app.com.thetechnocafe.linkshortner.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gurleensethi on 04/03/17.
 */

public class LinksRecyclerAdapter extends RecyclerView.Adapter<LinksRecyclerAdapter.LinksViewHolder> {

    private Context mContext;
    private List<ShortLink> mShortLinks;

    public LinksRecyclerAdapter(Context context, List<ShortLink> shortLinks) {
        mContext = context;
        mShortLinks = shortLinks;
    }

    //View Holder
    class LinksViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.short_link_text_view)
        TextView mShortLinkTextView;
        @BindView(R.id.copy_image_view)
        ImageView mCopyImageView;
        private int mPosition;

        LinksViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            //Copy the link to clipboard
            mCopyImageView.setOnClickListener(view -> {
                //Get the clipboard service
                ClipboardManager clipboardManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);

                //Create a clip
                ClipData clipData = ClipData.newPlainText("Short Link", mShortLinks.get(mPosition).getId());

                //Copy to clipboard
                clipboardManager.setPrimaryClip(clipData);

                //Notify user with toast
                Toast.makeText(mContext, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show();
            });
        }

        //Bind data to item
        void bindData(int position) {
            mPosition = position;

            mShortLinkTextView.setText(mShortLinks.get(position).getId());
        }
    }

    @Override
    public LinksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recycler_short_link, parent, false);
        return new LinksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LinksViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return mShortLinks.size();
    }
}
