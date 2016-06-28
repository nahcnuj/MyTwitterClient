package jp.nobody.nahcnuj.mytwitterclient;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private LayoutInflater mLayoutInflater;
    private List<Tweet> mTweetList;
    private int mLoadedNumOfTweets;
    private final int MAX_SEARCH_TWEETS = 1000;
    private Long maxId = -1L;

    public RecyclerAdapter(Context context) {
        Log.v("MTC", "RecyclerAdapter()");
        this.mLayoutInflater = LayoutInflater.from(context);

        this.mTweetList = Collections.synchronizedList(new ArrayList<Tweet>());

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(context, new Twitter(authConfig));

        this.mLoadedNumOfTweets = 0;

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
        if (this.mTweetList == null || this.mTweetList.size() <= position || this.mTweetList.get(position) == null) return;
        viewHolder.mName.setText(mTweetList.get(position).user.name);
        viewHolder.mScreenName.setText("@"+mTweetList.get(position).user.screenName);
        viewHolder.mTweet.setText(mTweetList.get(position).text);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        if (this.mTweetList == null) return 0;

        return this.mTweetList.size();
    }

    public void getTweets() {
        if (this.mLoadedNumOfTweets >= MAX_SEARCH_TWEETS) return;

        TwitterApiClient client = TwitterCore.getInstance().getApiClient();

        client.getSearchService().tweets(
                "iQON", null, "ja", "ja", "recent", Math.min(100, MAX_SEARCH_TWEETS - this.mLoadedNumOfTweets), null, null, maxId == -1L ? null : maxId, true,
                new GuestCallback<>(new Callback<Search>() {
                    @Override
                    public void success(Result<Search> result) {
                        mTweetList.addAll(result.data.tweets);

                        notifyItemRangeInserted(mLoadedNumOfTweets, 100);

                        mLoadedNumOfTweets = mTweetList.size();

                        if (result.data.searchMetadata.nextResults == null) {
                            return;
                        }

                        Map<String,String> nextResults = new LinkedHashMap<>();
                        for (String pair : result.data.searchMetadata.nextResults.substring(1).split("&")) {
                            String kv[] = pair.split("=");
                            nextResults.put(kv[0],kv[1]);
                        }

                        maxId = Long.parseLong(nextResults.get("max_id"));
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        exception.printStackTrace();
                    }
                })
        );
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
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

    private static final String TWITTER_KEY = "4PPk44OhJkCEVkrnuX5QK0tQl";
    private static final String TWITTER_SECRET = "CD7SKVj3kDKV0GDerHF4aSXGk9O41AkBRyLrpw203jP8F14thW";
}
