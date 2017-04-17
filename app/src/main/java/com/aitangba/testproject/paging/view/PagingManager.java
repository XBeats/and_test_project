package com.aitangba.testproject.paging.view;

import com.aitangba.testproject.paging.OnDataChangeListener;

/**
 * Created by XBeats on 2017/3/26.
 */

public interface PagingManager {

    void setNeverLoadMore(boolean neverLoadMore);

    void finishLoadMore();

    void scrollLoadMore();

    void setOnDataChangedListener(OnDataChangeListener dataChangedListener);

}