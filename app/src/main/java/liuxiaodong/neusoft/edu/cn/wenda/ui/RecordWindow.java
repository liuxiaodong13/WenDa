package liuxiaodong.neusoft.edu.cn.wenda.ui;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import liuxiaodong.neusoft.edu.cn.wenda.R;

/**
 * Created by DONG on 2017/3/25.
 */

public class RecordWindow extends PopupWindow {
    /**
     * 布局文件的最外层View
     */
    protected View mContentView;
    protected Context context;

    private AudioRecorderButton mAudioRecorderButton;

    public AudioRecorderButton getAudioRecorderButton() {
        return mAudioRecorderButton;
    }

    public RecordWindow(Context context, boolean focusable) {
        mContentView = LayoutInflater.from(context).inflate(R.layout.record_window, null);
        mAudioRecorderButton = (AudioRecorderButton) mContentView.findViewById(R.id.btn_record);
        setContentView(mContentView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.context = context;
        this.setFocusable(focusable);
        setBackgroundDrawable(new BitmapDrawable());
        setTouchable(true);
        setOutsideTouchable(true);
        setTouchInterceptor(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE)
                {
                    dismiss();
                    return true;
                }
                return false;
            }
        });

    }



    protected static int dpToPx(Context context, int dp)
    {
        return (int) (context.getResources().getDisplayMetrics().density * dp + 0.5f);
    }

}
