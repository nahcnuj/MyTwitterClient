package jp.nobody.nahcnuj.mytwitterclient;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Observable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.fabric.sdk.android.Fabric;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private LayoutInflater mLayoutInflater;
    private List<Tweet> mTweetList;

    public RecyclerAdapter(Context context) {
        Log.v("MTC", "RecyclerAdapter()");
        mLayoutInflater = LayoutInflater.from(context);

        mTweetList = Collections.synchronizedList(new ArrayList<Tweet>());

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(context, new Twitter(authConfig));

        TwitterCore.getInstance().logInGuest(new Callback<AppSession>() {
            @Override
            public void success(Result<AppSession> result) {
                getTweets();
            }

            @Override
            public void failure(TwitterException exception) {
                exception.printStackTrace();
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.tweet_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        if (mTweetList == null || mTweetList.size() <= position || mTweetList.get(position) == null) return;

        viewHolder.mTextView.setText(mTweetList.get(position).text);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        if (mTweetList == null) return 0;

        return mTweetList.size();
    }

    private void getTweets() {
        TwitterApiClient client = TwitterCore.getInstance().getApiClient();

        client.getSearchService().tweets("iQON", null, "ja", "ja", "recent", 100, null, null, null, true, new GuestCallback<Search>(new Callback<Search>() {
            @Override
            public void success(Result<Search> result) {
                mTweetList.addAll(result.data.tweets);

                notifyDataSetChanged();
            }

            @Override
            public void failure(TwitterException exception) {
                exception.printStackTrace();
            }
        }));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tweet)
        public TextView mTextView;

        public ViewHolder(View v) {
            super(v);

            ButterKnife.bind(this, v);
        }
    }

    private static final String TWITTER_KEY = "4PPk44OhJkCEVkrnuX5QK0tQl";
    private static final String TWITTER_SECRET = "CD7SKVj3kDKV0GDerHF4aSXGk9O41AkBRyLrpw203jP8F14thW";
}
