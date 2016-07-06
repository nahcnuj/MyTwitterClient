package jp.nobody.nahcnuj.mytwitterclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.GuestCallback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Search;

import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final int MAX_SEARCH_TWEETS = 1000;

    @BindView(R.id.recycler_view)
    public RecyclerView mRecyclerView;

    private int mLoadedNumOfTweets = 0;
    private Long mMaxId = -1L;
    private boolean mWasLoadedAllTweets = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        RecyclerView.Adapter adapter = new RecyclerAdapter(this.getApplicationContext());
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addOnScrollListener(new ScrollPagerListener((LinearLayoutManager) mRecyclerView.getLayoutManager()) {
            @Override
            public void load() {
                getTweets();
            }
        });

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

    public void getTweets() {
        if (this.mLoadedNumOfTweets >= MAX_SEARCH_TWEETS || this.mWasLoadedAllTweets) {
            return;
        }

        TwitterApiClient client = TwitterCore.getInstance().getApiClient();

        client.getSearchService().tweets(
                "iQON", null, "ja", "ja", "recent", Math.min(100, MAX_SEARCH_TWEETS - this.mLoadedNumOfTweets), null, null, mMaxId == -1L ? null : mMaxId, true,
                new GuestCallback<>(new Callback<Search>() {
                    @Override
                    public void success(Result<Search> result) {
                        ((RecyclerAdapter) mRecyclerView.getAdapter()).addDataOf(result.data.tweets);

                        mRecyclerView.getAdapter().notifyItemRangeInserted(mLoadedNumOfTweets, 100);

                        mLoadedNumOfTweets = mRecyclerView.getAdapter().getItemCount();

                        if (result.data.searchMetadata.nextResults == null) {
                            mWasLoadedAllTweets = true;
                            return;
                        }

                        Map<String, String> nextResults = new LinkedHashMap<>();
                        for (String pair : result.data.searchMetadata.nextResults.substring(1).split("&")) {
                            String kv[] = pair.split("=");
                            nextResults.put(kv[0], kv[1]);
                        }

                        mMaxId = Long.parseLong(nextResults.get("max_id"));
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        exception.printStackTrace();
                    }
                })
        );
    }
}
