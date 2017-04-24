package liuxiaodong.neusoft.edu.cn.wenda.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import liuxiaodong.neusoft.edu.cn.wenda.R;

/**
 * Created by DONG on 2017/1/12.
 */

public class NotificationView extends FrameLayout {
    @BindView(R.id.civ_isRead)
    CircleImageView civIsRead;
    @BindView(R.id.tv_notification_title)
    TextView tvTitle;
    @BindView(R.id.tv_notification_content)
    TextView tvContent;
    @BindView(R.id.cv_item)
    CardView cvItem;
    private boolean isRead;
    private String title;
    private String content;

    public NotificationView(Context context) {
        super(context);
        init(context);

    }

    public NotificationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context);
    }

    public NotificationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NotificationView);
        isRead = a.getBoolean(R.styleable.NotificationView_isRead, false);
        title = a.getString(R.styleable.NotificationView_title);
        content = a.getString(R.styleable.NotificationView_content);
        a.recycle();
        init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.notification_item, this);
        ButterKnife.bind(this);
        setRead(isRead);
        setContent(content);
        setTitle(title);

    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        cvItem.setOnClickListener(l);
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        cvItem.setOnLongClickListener(l);
    }

    public void setRead(boolean read) {
        civIsRead.setImageResource(read ? 0 : R.color.colorAccent);
    }


    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void setContent(String content) {
        tvContent.setText(content);
    }


}
