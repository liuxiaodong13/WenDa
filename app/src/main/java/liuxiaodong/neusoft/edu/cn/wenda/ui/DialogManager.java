package liuxiaodong.neusoft.edu.cn.wenda.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import liuxiaodong.neusoft.edu.cn.wenda.R;


/**
 * Created by DONG on 2017/3/23.
 */

public class DialogManager {

    private Dialog mDialog;

    private ImageView mIcon;
    private ImageView mVoice;

    private TextView mLabel;

    private Context mContext;

    public DialogManager(Context mContext) {
        this.mContext = mContext;
    }

    public void showRecordingDialog() {
        mDialog = new Dialog(mContext, R.style.Theme_AudioDialog);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_recorder, null);
        mDialog.setContentView(view);

        mIcon = (ImageView) mDialog.findViewById(R.id.iv_recorder_dialog_icon);
        mVoice = (ImageView) mDialog.findViewById(R.id.iv_recorder_dialog_voice);
        mLabel = (TextView) mDialog.findViewById(R.id.tv_dialog_label);

        if (mDialog != null) {

            mDialog.show();
        }

    }

    /**
     * 正在录制
     */
    public void recording() {
        if (mDialog != null && mDialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.VISIBLE);
            mLabel.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.drawable.recorder);
            mVoice.setImageResource(R.drawable.v1);
            mLabel.setText(R.string.dialog_up_to_cancel);

        }
    }

    public void wantCancel() {
        if (mDialog != null && mDialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.GONE);
            mLabel.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.drawable.cancel);
            mLabel.setText(R.string.dialog_release_to_cancel);

        }

    }

    public void tooShort() {

        if (mDialog != null && mDialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.GONE);
            mLabel.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.drawable.voice_to_short);
            mLabel.setText(R.string.dialog_recordtime_too_short);

        }
    }

    public void dismissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }

    }

    public void updateVoiceLevel(int level) {
        if (mDialog != null && mDialog.isShowing()) {
//            mIcon.setVisibility(View.VISIBLE);
//            mVoice.setVisibility(View.VISIBLE);
//            mLabel.setVisibility(View.VISIBLE);

            int resId = mContext.getResources().getIdentifier("v" + level, "drawable", mContext.getPackageName());
            mVoice.setImageResource(resId);
        }

    }
}
