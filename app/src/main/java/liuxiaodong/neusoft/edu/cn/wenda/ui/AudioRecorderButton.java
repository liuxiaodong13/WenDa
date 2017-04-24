package liuxiaodong.neusoft.edu.cn.wenda.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import liuxiaodong.neusoft.edu.cn.wenda.R;
import liuxiaodong.neusoft.edu.cn.wenda.utils.MyUtils;


/**
 * Created by DONG on 2017/3/23.
 */

public class AudioRecorderButton extends android.support.v7.widget.AppCompatButton implements AudioManager.AudioStateListener {

    private static final int DISTANCE_Y_CANCEL = 50;

    private static final int MAX_VOICE_LEVEL = 7;

    private static final int TOO_SHORT_DIALOG_SHOWING_TIME = 1100;

    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_WANT_CANCEL = 3;

    private static final int MSG_AUDIO_PREPARED = 0x110;
    private static final int MSG_VOICE_CHANGED = 0x111;
    private static final int MSG_DIALOG_DISMISS = 0x112;


    private int mCurrentState = STATE_NORMAL;
    private boolean isRecording = false;

    private float mTime;

    private DialogManager mDialogManager;

    private AudioManager mAudioManager;
    private boolean mReady = false;

    public interface AudioFinishRecordListener {
        void onFinish(float seconds, String filePath);
    }

    private AudioFinishRecordListener mAudioFinishRecordListener;

    public void setAudioFinishRecordListener(AudioFinishRecordListener audioFinishRecordListener) {
        mAudioFinishRecordListener = audioFinishRecordListener;
    }

    public AudioRecorderButton(Context context) {
        this(context, null);
    }


    public AudioRecorderButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        mDialogManager = new DialogManager(context);

        String dir = MyUtils.getAudioFilesDir(context);

        mAudioManager = AudioManager.getInstance(dir);
        mAudioManager.setAudioStateListener(this);

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mReady = true;
                mAudioManager.prepareAudio();

                return false;
            }
        });

    }

    private Runnable mGetVoiceLevelRunnable = new Runnable() {
        @Override
        public void run() {
            while (isRecording) {
                try {
                    Thread.sleep(100);
                    mTime += 0.1f;
                    if (isRecording) {

                        mHandler.sendEmptyMessage(MSG_VOICE_CHANGED);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AUDIO_PREPARED:
                    isRecording = true;
                    changeState(STATE_RECORDING);

                    mDialogManager.showRecordingDialog();

                    new Thread(mGetVoiceLevelRunnable).start();

                    break;
                case MSG_VOICE_CHANGED:
                    mDialogManager.updateVoiceLevel(mAudioManager.getVoiceLevel(MAX_VOICE_LEVEL));

                    break;
                case MSG_DIALOG_DISMISS:
                    mDialogManager.dismissDialog();

                    break;
            }
        }
    };

    @Override
    public void wellPrepared() {
        mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_MOVE:
                if (isRecording) {
                    if (wantCancel(x, y)) {
                        changeState(STATE_WANT_CANCEL);

                    } else {
                        changeState(STATE_RECORDING);
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                //还没有触发长按事件
                if (!mReady) {
                    reset();
                    return super.onTouchEvent(event);
                }
                //还没有准备完成 就已经松开手指
                if (!isRecording || mTime < 0.8f) {
                    mDialogManager.tooShort();
                    mAudioManager.cancel();
                    mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DISMISS, TOO_SHORT_DIALOG_SHOWING_TIME);
                }

                //正常结束录制
                else if (mCurrentState == STATE_RECORDING) {
                    isRecording = false;
                    mDialogManager.dismissDialog();
                    mAudioManager.release();
                    if (mAudioFinishRecordListener != null)
                        mAudioFinishRecordListener.onFinish(mTime, mAudioManager.getCurrentFilePath());
                    //保存录制
                    //回调activity

                } else if (mCurrentState == STATE_WANT_CANCEL) {
                    mAudioManager.cancel();
                    mDialogManager.dismissDialog();
                    //r取消录制


                }

                reset();

                break;
        }

        return super.onTouchEvent(event);
    }

    /**
     * 复位标志位 和状态
     */
    private void reset() {
        changeState(STATE_NORMAL);
        isRecording = false;
        mReady = false;
        mTime = 0;

    }

    private boolean wantCancel(int x, int y) {
        if (x < 0 || x > getWidth()) {
            return true;
        }
        return y < -DISTANCE_Y_CANCEL || y > getHeight() + DISTANCE_Y_CANCEL;

    }

    /**
     * 改变按钮的背景和文字
     *
     * @param state
     */
    private void changeState(int state) {
        if (mCurrentState != state) {
            mCurrentState = state;
            switch (state) {
                case STATE_NORMAL:
                    setBackgroundResource(R.drawable.button_recorder_normal);
                    setText(R.string.str_recorder_normal);
                    break;
                case STATE_RECORDING:
                    setBackgroundResource(R.drawable.button_recording);
                    setText(R.string.str_recorder_recording);
                    if (isRecording) {
                        mDialogManager.recording();

                    }
                    break;
                case STATE_WANT_CANCEL:
                    setBackgroundResource(R.drawable.button_recording);
                    setText(R.string.str_recorder_want_cancel);
                    mDialogManager.wantCancel();
                    break;
            }

        }

    }


}
