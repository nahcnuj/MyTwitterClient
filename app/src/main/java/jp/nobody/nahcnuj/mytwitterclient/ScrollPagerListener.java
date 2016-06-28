package jp.nobody.nahcnuj.mytwitterclient;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class ScrollPagerListener extends RecyclerView.OnScrollListener {
    private LinearLayoutManager mLayoutManager;

    private int mPrevTotalCount = 0;
    private boolean mIsLoading = false;

    private final int LOAD_THRESHOLD = 10;

    public ScrollPagerListener(LinearLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (dx == 0 && dy == 0) return;

        int visibleItemCount = recyclerView.getChildCount();
        int totalItemCount = mLayoutManager.getItemCount();
        int firstVisibleItemPos = mLayoutManager.findFirstVisibleItemPosition();
        int lastVisibleItemPos = mLayoutManager.findLastVisibleItemPosition();

        if (totalItemCount != mPrevTotalCount) {
            mIsLoading = false;
        }

        if (mIsLoading) return;

        mPrevTotalCount = totalItemCount;

        if (totalItemCount - lastVisibleItemPos <= LOAD_THRESHOLD) {
            mIsLoading = true;

            load();
        }
    }

    public abstract void load();
}
