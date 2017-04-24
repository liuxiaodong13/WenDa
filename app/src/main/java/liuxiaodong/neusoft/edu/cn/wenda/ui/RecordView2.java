package liuxiaodong.neusoft.edu.cn.wenda.ui;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import liuxiaodong.neusoft.edu.cn.wenda.R;

/**
 * Created by DONG on 2017/3/24.
 */

public class RecordView2 extends RelativeLayout {
    @BindView(R.id.view_record_anim)
    View viewRecordAnim;
    @BindView(R.id.fl_container)
    FrameLayout flContainer;
    @BindView(R.id.tv_time)
    TextView tvTime;



    public interface playListener{
        void play();
        void stop();
    }

    private playListener mPlayListener;

    public void setPlayListener(playListener playListener) {
        mPlayListener = playListener;
    }

    public RecordView2(Context context) {
        this(context, null);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.item_record2, this);
        ButterKnife.bind(this);


    }

    public RecordView2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }



    public void setTime(String time) {
        tvTime.setText(time  + "'");

    }

    public void startAnim() {
        viewRecordAnim.setBackgroundResource(R.drawable.play_anim);
        AnimationDrawable anim = (AnimationDrawable) viewRecordAnim.getBackground();
        anim.start();

    }

    private boolean isAnimateRunning() {
        AnimationDrawable anim = (AnimationDrawable) viewRecordAnim.getBackground();
        return anim.isRunning();
    }

    public void stopAnim() {
        AnimationDrawable anim = (AnimationDrawable) viewRecordAnim.getBackground();
        if (anim.isRunning()) {
            anim.stop();
        }
        viewRecordAnim.setBackgroundResource(R.drawable.adj);

    }


}
