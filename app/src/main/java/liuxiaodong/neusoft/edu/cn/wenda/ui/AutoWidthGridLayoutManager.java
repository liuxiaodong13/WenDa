package liuxiaodong.neusoft.edu.cn.wenda.ui;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by DONG on 2016/10/28.
 */

public class AutoWidthGridLayoutManager extends GridLayoutManager {
    public AutoWidthGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public AutoWidthGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {

        super.onMeasure(recycler, state, widthSpec, heightSpec);
        int width = 0;
        int itemCount = state.getItemCount();
        for (int i = 0; i < itemCount; i++) {
            if (itemCount < getSpanCount()) {
                View view = getChildAt(i);
                measureChild(view, widthSpec, heightSpec);
                width += View.MeasureSpec.getSize(view.getMeasuredWidth());
            }
        }
        int measuredHeight = View.MeasureSpec.getSize(heightSpec);

        setMeasuredDimension(width, measuredHeight);

    }
}
