package jp.nobody.nahcnuj.mytwitterclient;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class ScrollPagerListener extends RecyclerView.OnScrollListener {
    private static final int LOAD_THRESHOLD = 10;

    private LinearLayoutManager mLayoutManager;

    private int mPrevTotalCount = 0;
    private boolean mIsLoading = false;

    public ScrollPagerListener(LinearLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (dx == 0 && dy == 0) {
            return;
        }

        int totalItemCount = mLayoutManager.getItemCount();
        int lastVisibleItemPos = mLayoutManager.findLastVisibleItemPosition();

        if (totalItemCount != mPrevTotalCount) {
            mIsLoading = false;
        }

        if (mIsLoading) {
            return;
        }

        mPrevTotalCount = totalItemCount;

        if (totalItemCount - lastVisibleItemPos <= LOAD_THRESHOLD) {
            mIsLoading = true;

            load();
        }
    }

    public abstract void load();
}
