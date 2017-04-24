package liuxiaodong.neusoft.edu.cn.wenda.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import liuxiaodong.neusoft.edu.cn.wenda.R;

/**
 * Created by DONG on 2016/10/22.
 */

public class PicSelectorToolbarHeaderView extends LinearLayout {
    //@BindView(R.id.tv_title)
    TextView tvTitle;
    //@BindView(R.id.tv_subtitle)
    TextView tvSubTitle;
    //@BindView(R.id.ll_bar)
    LinearLayout ll_bar;
    public PicSelectorToolbarHeaderView(Context context) {
        super(context, null);
    }

    public PicSelectorToolbarHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PicSelectorToolbarHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);

    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.pic_selector_bar_header, this, true);


    }
    public void setTitle(String s) {
        tvTitle.setText(s);
    }
    public void setSubTitle(String s) {
        tvSubTitle.setText(s);
    }

    public void setTitlesColor(int color) {
        tvTitle.setTextColor(color);
        tvSubTitle.setTextColor(color);
    }

    public void setTitleOnClickListener(OnClickListener listener) {
        ll_bar.setOnClickListener(listener);

    }
}
