package liuxiaodong.neusoft.edu.cn.wenda.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import liuxiaodong.neusoft.edu.cn.wenda.utils.Logger;
import liuxiaodong.neusoft.edu.cn.wenda.utils.MyUtils;

/**
 * Created by DONG on 2017/3/22.
 */

public class MyRecyclerView extends RecyclerView {

    private OnClickListener listener;
    private OnLongClickListener mOnLongClickListener;
    private int timeInterval = ViewConfiguration.getLongPressTimeout();
    private int currentTime;


    public void setClickBlankAreaListener(OnClickListener listener) {
        this.listener = listener;
    }


    public void setOnLongClickBlankAreaListener(OnLongClickListener onLongClickListener) {
        mOnLongClickListener = onLongClickListener;
    }

    public MyRecyclerView(Context context) {
        super(context);
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }



    @Override
    public boolean onTouchEvent(MotionEvent e) {
        LayoutManager layoutManager = getLayoutManager();


        if (layoutManager instanceof GridLayoutManager) {
            //说明当前是Grid布局
            int childCount = getChildCount();
            if (childCount > 0) {

//                GridLayoutManager layoutManager1 = (GridLayoutManager) layoutManager;
                int spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
                View child = getChildAt(0);
                int itemWidth = child.getMeasuredWidth();
                int gap = (MyUtils.getScreenMetrics(getContext()).widthPixels - spanCount * itemWidth) / 4;
                int contentX = (childCount % spanCount) * gap + (childCount % spanCount) * itemWidth;
                int contentY = (childCount / spanCount) * gap + (childCount / spanCount) * itemWidth;
                int x = (int) e.getX();
                int y = (int) e.getY();
                if (x >= contentX && y >= contentY) {
//                    if (e.getAction() == MotionEvent.ACTION_DOWN)
//                    {
//                    }
                    if (e.getAction() == MotionEvent.ACTION_UP) {
                        Logger.d("您点击了空白区域！");

                        onClick();
                        return true;


                    }
                    return super.onTouchEvent(e);

                }
                return super.onTouchEvent(e);
            }


            return super.onTouchEvent(e);

        }
        return super.onTouchEvent(e);

    }

    private void onClick() {
        if (listener != null) {
            listener.onClick(MyRecyclerView.this);
        }
    }

    private void onLongClick() {
        if (mOnLongClickListener != null) {
            mOnLongClickListener.onLongClick(MyRecyclerView.this);
        }
    }
}
