package liuxiaodong.neusoft.edu.cn.wenda.ui;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by DONG on 2017/3/23.
 */

public class AudioManager {
    private static final String TAG = "AudioManager";
    private MediaRecorder mMediaRecorder;
    private String mDir;
    private String mCurrentFilePath;

    private boolean isPrepared = false;

    public String getCurrentFilePath() {
        return mCurrentFilePath;
    }

    /**
     * aduio准备完毕时候 进行回调
     */
    public interface AudioStateListener {
        void wellPrepared();
    }

    private AudioStateListener mListener;

    public void setAudioStateListener(AudioStateListener listener) {
        mListener = listener;
    }

    private static AudioManager mInstance;

    private AudioManager(String dir) {
        this.mDir = dir;
    }

    public static AudioManager getInstance(String dir) {
        if (mInstance == null) {
            synchronized (AudioManager.class) {
                if (mInstance == null) {
                    mInstance = new AudioManager(dir);
                }
            }
        }
        return mInstance;
    }

    public void prepareAudio() {


        try {
            isPrepared = false;

            File dir = new File(mDir);
            if (!dir.exists())
                dir.mkdirs();
            String fileName = generateFileName();

            File file = new File(dir, fileName);

            mCurrentFilePath = file.getAbsolutePath();

            mMediaRecorder = new MediaRecorder();


            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);

            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            mMediaRecorder.setOutputFile(file.getAbsolutePath());

            mMediaRecorder.prepare();

            mMediaRecorder.start();
            isPrepared = true;
            if (mListener != null) {
                mListener.wellPrepared();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String generateFileName() {

        return UUID.randomUUID().toString() + ".amr";
    }

    public int getVoiceLevel(int maxLevel) {
        if (isPrepared) {
            //1-32767
            try {
                if (mMediaRecorder != null) {

                    return maxLevel * mMediaRecorder.getMaxAmplitude() / 32768 + 1;
                }
            } catch (IllegalStateException e) {
            }
        }

        return 1;
    }

    public void release() {
        mMediaRecorder.stop();
        mMediaRecorder.release();
        Log.d(TAG, "release: filePath:" + mCurrentFilePath);
        mMediaRecorder = null;

    }

    public void cancel() {
        release();
        //删除文件
        if (mCurrentFilePath != null) {

            File file = new File(mCurrentFilePath);
            file.delete();
            mCurrentFilePath = null;
        }



    }


}
