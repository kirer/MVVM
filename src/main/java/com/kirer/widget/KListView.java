package com.kirer.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;


import com.kirer.KAdapter;
import com.kirer.utils.LUtil;

import java.util.ArrayList;

/**
 * Created by xinwb on 2016/8/22.
 */
public class KListView extends FrameLayout implements SwipeRefreshLayout.OnRefreshListener {

    private boolean loadMoreEnabled = true;
    private boolean isLoading = false;

    public KListView(Context context) {
        super(context);
        init();
    }

    public KListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public KListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private SwipeRefreshLayout swipeView;
    private RecyclerView listView;
    private LinearLayout footerView;

    private void init() {
        initSwipeView();
        initListView();
        initLoadMoreView();
        addView(swipeView);
    }

    private void initSwipeView() {
        swipeView = new SwipeRefreshLayout(getContext());
        swipeView.setOnRefreshListener(this);
    }

    private void initListView() {
        listView = new RecyclerView(getContext());
        listView.addOnScrollListener(new OnScrollListener());
        swipeView.addView(listView);
    }

    private void initLoadMoreView() {
        footerView = new LinearLayout(getContext());
        footerView.setGravity(Gravity.CENTER);
        footerView.setPadding(10, 10, 10, 10);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.BOTTOM;
        footerView.setLayoutParams(lp);
        ProgressBar progressBar = new ProgressBar(getContext());
//        progressBar.setLayoutParams(new ViewGroup.LayoutParams(DisplayUtils.dp2px(36),DisplayUtils.dp2px(36)));
        footerView.addView(progressBar);
//        TextView tipTv = new TextView(getContext());
//        tipTv.setText("正在加载...");
//        footerView.addView(tipTv);
        footerView.setVisibility(GONE);
//        footerView.setBackgroundColor(Color.parseColor("#e7e7e7"));
        addFooterView(footerView);
    }

    private boolean shouldAdjustSpanSize = false;

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        listView.setLayoutManager(layoutManager);
        if (layoutManager instanceof GridLayoutManager || layoutManager instanceof StaggeredGridLayoutManager) {
            this.shouldAdjustSpanSize = true;
        }
    }

    private KAdapter adapter;
    private ArrayList<View> mTmpHeaderView = new ArrayList<>();
    private ArrayList<View> mTmpFooterView = new ArrayList<>();

    public void setAdapter(KAdapter adapter) {
        if (null == adapter) {
            throw new IllegalArgumentException("adapter must not be null!");
        }
        this.adapter = adapter;
        if (shouldAdjustSpanSize) {
            this.adapter.adjustSpanSize(listView);
        }
        if (mTmpHeaderView.size() > 0) {
            for (View view : mTmpHeaderView) {
                addHeaderView(view);
            }
            mTmpHeaderView.clear();
        }
        if (mTmpFooterView.size() > 0) {
            for (View view : mTmpFooterView) {
                addFooterView(view);
            }
            mTmpFooterView.clear();
        }
        listView.setAdapter(this.adapter);
    }
    public KAdapter getAdapter(){
        return adapter;
    }

    public void addHeaderView(View view) {
        if (null == view) {
            throw new IllegalArgumentException("the view to add must not be null!");
        } else if (adapter == null) {
            mTmpHeaderView.add(view);
        } else {
            adapter.addHeaderView(view);
        }
    }

    public void addFooterView(View view) {
        if (null == view) {
            throw new IllegalArgumentException("the view to add must not be null!");
        } else if (adapter == null) {
            mTmpFooterView.add(view);
        } else {
            adapter.addFooterView(view, true);
        }
    }

    public void setLoadMoreEnabled(boolean enabled) {
        this.loadMoreEnabled = enabled;
    }

    public void setRefreshingEnabled(boolean enabled) {
        this.swipeView.setEnabled(enabled);
    }

    public void setRefreshing(boolean refreshing) {
        LUtil.d("setRefreshing --> " + refreshing);
        this.swipeView.setRefreshing(refreshing);
    }

    public void setLoadingMore(boolean loadingMore) {
        LUtil.d("setLoadingMore --> " + loadingMore);
        isLoading = loadingMore;
        this.footerView.setVisibility(loadingMore ? VISIBLE : GONE);
    }

    private LoadingListener listener;

    public void setLoadingListener(LoadingListener listener) {
        this.listener = listener;
    }

    public interface LoadingListener {
        void onRefresh();

        void onLoadMore();
    }

    @Override
    public void onRefresh() {
        if (listener != null) {
            listener.onRefresh();
        }
    }

    public void addOnScrollListener(RecyclerView.OnScrollListener listener) {
        this.listView.addOnScrollListener(listener);
    }

    private class OnScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (RecyclerView.SCROLL_STATE_IDLE != newState || listener == null || !loadMoreEnabled) {
                return;
            }
            RecyclerView.LayoutManager layoutManager = listView.getLayoutManager();
            int lastVisibleItemPosition;
            if (layoutManager instanceof GridLayoutManager) {
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                int[] into = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(into);
                lastVisibleItemPosition = findMax(into);
            } else {
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            }
            if (layoutManager.getChildCount() > 0 && lastVisibleItemPosition >= layoutManager.getItemCount() - 1 && layoutManager.getItemCount() > layoutManager.getChildCount()) {
                isLoading = true;
                footerView.setVisibility(View.VISIBLE);
                listener.onLoadMore();
            }
        }

        private int findMax(int[] lastPositions) {
            int max = lastPositions[0];
            for (int value : lastPositions) {
                if (value > max) {
                    max = value;
                }
            }
            return max;
        }
    }
}
