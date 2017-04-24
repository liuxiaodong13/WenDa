package liuxiaodong.neusoft.edu.cn.wenda.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import liuxiaodong.neusoft.edu.cn.wenda.R;

/**
 * Created by DONG on 2017/3/11.
 */

public class UserIconView extends FrameLayout {
    @BindView(R.id.civ_user)
    CircleImageView civUser;
    @BindView(R.id.iv_gender)
    ImageView ivGender;
    @BindView(R.id.rl_container)
    RelativeLayout rlContainer;
    //性别 是男则为true
    private boolean gender = true;

    public UserIconView(Context context) {
        super(context);
        init(context);
    }

    public UserIconView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context);
    }

    public UserIconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.UserIconView);
        gender = a.getBoolean(R.styleable.UserIconView_gender, true);

        a.recycle();
        init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.user_icon_view, this);
        ButterKnife.bind(this);
        setGenderSrc();

    }


    public void setGender(boolean isMale) {
        gender = isMale;
        setGenderSrc();

    }

    private void setGenderSrc() {
        ivGender.setImageResource(gender ? R.drawable.ic_boys : R.drawable.ic_girls);
    }

    public void setUserIcon() {

    }

    public CircleImageView getCivUserIcon() {
        return civUser;

    }

}
