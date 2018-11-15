package com.aitangba.testproject.paging.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.aitangba.testproject.R;
import com.aitangba.testproject.paging.PageBean;
import com.aitangba.testproject.paging.Response;


/**
 * Created by fhf11991 on 2017/5/11.
 */

public class PagingListView extends ListView implements PagingManager {

    private OnScrollListener mOnScrollListener;
    private FooterViewHolder mFooterViewHolder;

    private PagingHelper mPagingHelper = new PagingHelper();

    public PagingListView(Context context) {
        this(context, null);
    }

    public PagingListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View footerView = LayoutInflater.from(context).inflate(R.layout.layout_footer_view, null);
        setFooterViewHolder(new FooterViewHolder(footerView));

        super.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(mOnScrollListener != null) {
                    mOnScrollListener.onScrollStateChanged(view, scrollState);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(mOnScrollListener != null) {
                    mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                }

                mPagingHelper.onScrolled(view.getLastVisiblePosition() + 1 ==  view.getCount());
            }
        });
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mOnScrollListener = l;
    }

    @Override
    public void setOnLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        mPagingHelper.setOnLoadMoreListener(loadMoreListener);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
    }

    @Override
    public void checkPaging(Response response) {
        boolean hasMoreData = mPagingHelper.finishLoadMore(response.array.size());
        updateFooterStatus(hasMoreData);
    }

    @Override
    public PageBean getPageBean() {
        return mPagingHelper.getPageBean();
    }

    public void setFooterViewHolder(FooterViewHolder footerViewHolder) {
        mFooterViewHolder = footerViewHolder;
        addFooterView(mFooterViewHolder.itemView);
    }

    private void updateFooterStatus(boolean hasMoreData) {
        if(mFooterViewHolder == null) {
            return;
        }
        mFooterViewHolder.bindView(hasMoreData);
    }
}
