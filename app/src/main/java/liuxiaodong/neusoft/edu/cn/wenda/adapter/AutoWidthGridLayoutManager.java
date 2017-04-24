package liuxiaodong.neusoft.edu.cn.wenda.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by DONG on 2017/3/22.
 */

public class AutoWidthGridLayoutManager extends GridLayoutManager {
    public AutoWidthGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public AutoWidthGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public AutoWidthGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
       int width = 0;
        int childCount = getItemCount();
        for (int i = 0; i < childCount; i++) {
            View child = recycler.getViewForPosition(i);
            measureChild(child, widthSpec, heightSpec);

        }

    }
}
