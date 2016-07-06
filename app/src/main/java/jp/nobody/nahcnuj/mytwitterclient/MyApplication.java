package jp.nobody.nahcnuj.mytwitterclient;

import android.app.Application;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;

public class MyApplication extends Application {
    private static final String TWITTER_KEY = "4PPk44OhJkCEVkrnuX5QK0tQl";
    private static final String TWITTER_SECRET = "CD7SKVj3kDKV0GDerHF4aSXGk9O41AkBRyLrpw203jP8F14thW";

    @Override
    public void onCreate() {
        super.onCreate();

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
    }
}
