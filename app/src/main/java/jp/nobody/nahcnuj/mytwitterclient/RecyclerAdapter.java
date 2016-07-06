package jp.nobody.nahcnuj.mytwitterclient;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.GuestCallback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.*;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<Tweet> mTweetList;

    public RecyclerAdapter(Context context) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);

        this.mTweetList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.tweet_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        if (this.mTweetList == null || this.mTweetList.size() <= position || this.mTweetList.get(position) == null) {
            return;
        }
        Tweet tweet = mTweetList.get(position);
        Glide.with(mContext).load(tweet.user.profileImageUrl).into(viewHolder.mProfileImage);
        viewHolder.mName.setText(tweet.user.name);
        viewHolder.mScreenName.setText("@"+tweet.user.screenName);
        viewHolder.mTweet.setText(tweet.text);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        if (this.mTweetList == null) {
            return 0;
        }

        return this.mTweetList.size();
    }

    public void addDataOf(List<Tweet> tweets) {
        this.mTweetList.addAll(tweets);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tweet_view)
        public RelativeLayout mTweetView;
        @BindView(R.id.profile_image)
        public ImageView mProfileImage;
        @BindView(R.id.tweet)
        public TextView mTweet;
        @BindView(R.id.name)
        public TextView mName;
        @BindView(R.id.screen_name)
        public TextView mScreenName;

        public ViewHolder(View v) {
            super(v);

            ButterKnife.bind(this, v);
        }
    }
}
