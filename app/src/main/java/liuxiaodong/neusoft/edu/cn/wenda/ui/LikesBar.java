package liuxiaodong.neusoft.edu.cn.wenda.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import liuxiaodong.neusoft.edu.cn.wenda.R;

/**
 * Created by DONG on 2016/11/7.
 */

public class LikesBar extends LinearLayout {

    @BindView(R.id.tv_likes_count)
    TextView tvLikesCount;
    @BindView(R.id.iv_likes_image)
    ImageView ivLikesImage;
    private boolean isLike = false;
    private Integer count = 0;

    private onLikeStateChangedListener onLikeStateChangedListener;

    public LikesBar(Context context) {
        super(context);
        init();
    }

    public LikesBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LikesBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View.inflate(getContext(), R.layout.likes_bar, this);

      /*  ivLikesImage = (ImageView) findViewById(R.id.iv_likes_image);
        tvLikesCount  = (TextView) findViewById(R.id.tv_likes_count);*/
        ButterKnife.bind(this);
        ivLikesImage.setImageResource(R.drawable.ic_unlike);
        tvLikesCount.setText("0");
        ivLikesImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLike) { //取消点赞
                    ivLikesImage.setImageResource(R.drawable.ic_unlike);
                    count--;
                    tvLikesCount.setText(count + "");
                    isLike = false;
                    if (onLikeStateChangedListener != null) {
                        onLikeStateChangedListener.onCancelLike();
                    }
                    //Integer.parseInt(tvLikesCount.getText().toString());
                } else {
                    //点赞
                    ivLikesImage.setImageResource(R.drawable.ic_like);
                    isLike = true;
                    count++;
                    tvLikesCount.setText(count + "");
                    if (onLikeStateChangedListener != null) {
                        onLikeStateChangedListener.onLike();
                    }

                }

            }
        });
    }

    public void setOnLikeStateChangedListener(LikesBar.onLikeStateChangedListener onLikeStateChangedListener) {
        this.onLikeStateChangedListener = onLikeStateChangedListener;
    }

    public void setTvLikesCount(String count) {
        tvLikesCount.setText(count);
    }

    public void setState(boolean state) {
        isLike = state;
    }

    public interface onLikeStateChangedListener {
        void onLike();

        void onCancelLike();
    }


    public ImageView getImageView() {
        return ivLikesImage;
    }

    public void setState(Integer count, boolean isLike) {
        this.count = count;
        this.isLike = isLike;
        refresh();


    }

    private void refresh() {

        ivLikesImage.setImageResource(isLike ? R.drawable.ic_like : R.drawable.ic_unlike);

    }

}
