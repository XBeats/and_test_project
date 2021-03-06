package com.aitangba.testproject.view.calendar.common.manager;

import android.view.View;

import com.aitangba.testproject.view.calendar.common.pojo.CellBean;
import com.aitangba.testproject.view.calendar.common.pojo.WeekBean;

import java.util.Date;
import java.util.List;

/**
 * Created by fhf11991 on 2018/6/5
 */
public class RangeChoiceManager extends BaseHolidayManager {

    private int mRangeSize;

    public RangeChoiceManager(int rangeSize) {
        mRangeSize = rangeSize;
    }

    @Override
    public void onClick(View cellView, CellBean cellBean) {
        List<CellBean> selectedCells = monthAdapter.getSelectedCell();
        int size = selectedCells.size();
        if (size == 0) {
            cellBean.isSelected = !cellBean.isSelected;
            monthAdapter.notifyDataSetChanged();
        } else if (size == 1) {
            CellBean selectedCell = selectedCells.get(0);
            Date selectedDate = selectedCell.date;
            Date date = cellBean.date;

            Date firstSelectedDate;
            Date lastSelectedDate;
            if (selectedDate.before(date)) {
                if(mRangeSize != -1 && !date.before(getDateLater(selectedDate, mRangeSize))) {
                    if(mCellSelectableFilter != null && mCellSelectableFilter.onBeyond()) {
                        return;
                    }
                }
                firstSelectedDate = selectedDate;
                lastSelectedDate = date;
            } else if(selectedDate.after(date)){
                selectedCell.isSelected = false;
                cellBean.isSelected = true;
                monthAdapter.notifyDataSetChanged();
                return;
            } else { // 再次点击取消选中
                selectedCell.isSelected = false;
                monthAdapter.notifyDataSetChanged();
                return;
            }

            retry:
            for (int i = 0, count = monthAdapter.getItemCount(); i < count; i++) {
                WeekBean item = monthAdapter.getItem(i);
                for (int j = 0, itemCount = item.cellBeans.size(); j < itemCount; j++) {
                    CellBean tempCell = item.cellBeans.get(j);
                    Date tempDate = tempCell.date;
                    if(tempDate.compareTo(firstSelectedDate) == 0) {
                        tempCell.isSelected = true;
                        tempCell.option = CellBean.OPTION_FIRST;
                    } else if (tempDate.after(firstSelectedDate) && tempDate.before(lastSelectedDate)) {
                        tempCell.isSelected = true;
                        tempCell.option = CellBean.OPTION_MIDDLE;
                    } else if(tempDate.compareTo(lastSelectedDate) == 0) {
                        tempCell.isSelected = true;
                        tempCell.option = CellBean.OPTION_LAST;
                        break retry;
                    }
                }
            }
            monthAdapter.notifyDataSetChanged();
        } else {
            for (CellBean itemCell : selectedCells) {
                itemCell.isSelected = false;
                itemCell.option = CellBean.OPTION_NONE;
            }
            cellBean.isSelected = true;
            monthAdapter.notifyDataSetChanged();
        }
    }

    private CellSelectableFilter mCellSelectableFilter;

    public void setCellSelectableFilter(CellSelectableFilter cellSelectableFilter) {
        mCellSelectableFilter = cellSelectableFilter;
    }

    public interface CellSelectableFilter {
        boolean onBeyond();
    }
}
