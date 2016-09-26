package com.kirer;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kirer.utils.LUtil;
import com.kirer.widget.KListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xinwb on 2016/8/23.
 */
public abstract class KAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * ****************************************************
     * 需要实现的方法
     * ****************************************************
     */
    /**
     * Set viewType
     *
     * @param position
     */
    public int setItemViewType(int position){
        return position;
    }
    /**
     * Get layout_id by viewType
     *
     * @param viewType
     */
    public abstract int getLayoutId(int viewType);
    /**
     * Bind data to view
     *
     * @param holder
     * @param position
     */
    public abstract void bind(BindingHolder holder, int position);

    /**
     * ****************************************************
     * 初始化和数据相关
     * ****************************************************
     */
    protected List<T> dataList;
    public KAdapter() {
    }
    public KAdapter(List<T> dataList) {
        this.dataList = dataList;
    }
    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }
    public List<T> getDataList() {
        if (this.dataList == null) {
            this.dataList = new ArrayList<>();
        }
        return this.dataList;
    }
    public void addDataList(List<T> dataList) {
        if (this.dataList == null) {
            this.dataList = new ArrayList<>();
        }
        this.dataList.addAll(dataList);
        notifyDataSetChanged();
    }
    /**
     * ****************************************************
     * 默认方法
     * ****************************************************
     */
    @Override
    public int getItemViewType(int position) {
        if (isHeaderPosition(position)) {
            return headerViewInfoList.get(position).viewType;
        } else if (isFooterPosition(position)) {
            return footerViewInfoList.get(position - headerViewInfoList.size() - getDataList().size()).viewType;
        } else {
            return setItemViewType(position - headerViewInfoList.size());
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (isHeader(viewType)) {
            int whichHeader = Math.abs(viewType - BASE_HEADER_VIEW_TYPE);
            View headerView = headerViewInfoList.get(whichHeader).view;
            return createHeaderFooterViewHolder(headerView);
        } else if (isFooter(viewType)) {
            int whichFooter = Math.abs(viewType - BASE_FOOTER_VIEW_TYPE);
            View footerView = footerViewInfoList.get(whichFooter).view;
            return createHeaderFooterViewHolder(footerView);
        } else {
            return new BindingHolder(LayoutInflater.from(parent.getContext()).inflate(getLayoutId(viewType), parent, false));
        }
    }
    private RecyclerView.ViewHolder createHeaderFooterViewHolder(View view) {
        if (isStaggeredGrid) {
            StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams(
                    StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT, StaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT);
            params.setFullSpan(true);
            view.setLayoutParams(params);
        }
        return new RecyclerView.ViewHolder(view) {
        };
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        if (position < headerViewInfoList.size()) {
            // Headers don't need anything special
        } else if (position < headerViewInfoList.size() + getDataList().size()) {
            // This is a real position, not a header or footer. Bind it.
            bind((BindingHolder) holder, position - headerViewInfoList.size());
        } else {
            // Footers don't need anything special
        }
    }
    @Override
    public int getItemCount() {
        return headerViewInfoList.size() + getDataList().size() + footerViewInfoList.size();
    }
    /**
     * ****************************************************
     * 头部，脚部相关
     * ****************************************************
     */
    // Defines available view type integers for headers and footers.
    private static final int BASE_HEADER_VIEW_TYPE = -1 << 10;
    private static final int BASE_FOOTER_VIEW_TYPE = -1 << 11;
    private ArrayList<FixedViewInfo> headerViewInfoList = new ArrayList<>();
    private ArrayList<FixedViewInfo> footerViewInfoList = new ArrayList<>();
    /**
     * A class that represents a fixed view in a list, for example a header at the top
     * or a footer at the bottom.
     */
    public class FixedViewInfo {
        /**
         * The view to add to the list
         */
        public View view;
        /**
         * The data backing the view. This is returned from {RecyclerView.Adapter#getItemViewType(int)}.
         */
        public int viewType;
    }
    /**
     * Adds a header view
     *
     * @param view
     */
    public void addHeaderView(View view) {
        if (null == view) {
            throw new IllegalArgumentException("the view to add must not be null!");
        }
        final FixedViewInfo info = new FixedViewInfo();
        info.view = view;
        info.viewType = BASE_HEADER_VIEW_TYPE + headerViewInfoList.size();
        headerViewInfoList.add(info);
        notifyDataSetChanged();
    }
    /**
     * Adds a footer view
     *
     * @param view
     */
    public void addFooterView(View view) {
        addFooterView(view, false);
    }
    /**
     * Adds a footer view
     *
     * @param view
     */
    public void addFooterView(View view, boolean reverse) {
        if (null == view) {
            throw new IllegalArgumentException("the view to add must not be null!");
        }
        final FixedViewInfo info = new FixedViewInfo();
        info.view = view;
        info.viewType = BASE_FOOTER_VIEW_TYPE + footerViewInfoList.size();
        footerViewInfoList.add(info);
        if (reverse) {
            for (int i = 0; i < footerViewInfoList.size(); i++) {
                FixedViewInfo fixedViewInfo = footerViewInfoList.get(i);
                fixedViewInfo.viewType = BASE_FOOTER_VIEW_TYPE + footerViewInfoList.size() - i - 1;
            }
        }
        notifyDataSetChanged();
    }
    /**
     * gets the headers view
     *
     * @return List:
     * @version 1.0
     */
    public List<View> getHeadersView() {
        List<View> viewList = new ArrayList<View>(getHeadersCount());
        for (FixedViewInfo fixedViewInfo : headerViewInfoList) {
            viewList.add(fixedViewInfo.view);
        }
        return viewList;
    }
    /**
     * gets the footers view
     *
     * @return List:
     * @version 1.0
     */
    public List<View> getFootersView() {
        List<View> viewList = new ArrayList<View>(getHeadersCount());
        for (FixedViewInfo fixedViewInfo : footerViewInfoList) {
            viewList.add(fixedViewInfo.view);
        }
        return viewList;
    }
    /**
     * Setting the visibility of the header views
     *
     * @param shouldShow
     * @version 1.0
     */
    public void setHeaderVisibility(boolean shouldShow) {
        for (FixedViewInfo fixedViewInfo : headerViewInfoList) {
            fixedViewInfo.view.setVisibility(shouldShow ? View.VISIBLE : View.GONE);
        }
        notifyDataSetChanged();
    }
    /**
     * Setting the visibility of the footer views
     *
     * @param shouldShow
     * @version 1.0
     */
    public void setFooterVisibility(boolean shouldShow) {
        for (FixedViewInfo fixedViewInfo : footerViewInfoList) {
            fixedViewInfo.view.setVisibility(shouldShow ? View.VISIBLE : View.GONE);
        }
        notifyDataSetChanged();
    }
    /**
     * get the count of headers
     *
     * @return number of headers
     * @version 1.0
     */
    public int getHeadersCount() {
        return headerViewInfoList.size();
    }
    /**
     * get the count of footers
     *
     * @return the number of footers
     * @version 1.0
     */
    public int getFootersCount() {
        return footerViewInfoList.size();
    }
    private boolean isHeader(int viewType) {
        return viewType >= BASE_HEADER_VIEW_TYPE && viewType < (BASE_HEADER_VIEW_TYPE + headerViewInfoList.size());
    }
    private boolean isFooter(int viewType) {
        return viewType >= BASE_FOOTER_VIEW_TYPE && viewType < (BASE_FOOTER_VIEW_TYPE + footerViewInfoList.size());
    }
    private boolean isHeaderPosition(int position) {
        return position < headerViewInfoList.size();
    }
    private boolean isFooterPosition(int position) {
        return position >= headerViewInfoList.size() + getDataList().size();
    }

    /**
     * ****************************************************
     * GridView 头部，脚部LayoutParams
     * ****************************************************
     */
    private boolean isStaggeredGrid;
    private boolean shouldAdjustSpanSize = false;
    /**
     * adjust the GridLayoutManager SpanSize
     *
     * @param recycler
     * @version 1.0
     */
    public void adjustSpanSize(RecyclerView recycler) {
        if (recycler.getLayoutManager() instanceof GridLayoutManager) {
            final GridLayoutManager layoutManager = (GridLayoutManager) recycler.getLayoutManager();
            layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    boolean isHeaderOrFooter =
                            isHeaderPosition(position) || isFooterPosition(position);
                    return isHeaderOrFooter ? layoutManager.getSpanCount() : 1;
                }

            });
        }
        if (recycler.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            this.isStaggeredGrid = true;
        }
    }

    /**
     * ****************************************************
     * holder
     * ****************************************************
     */
    public class BindingHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {

        public T binding;

        public BindingHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }

}