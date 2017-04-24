package liuxiaodong.neusoft.edu.cn.wenda.other;

import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by DONG on 2017/3/24.
 */

public class MediaManager {
    private static MediaPlayer sMediaPlayer;



    private static boolean isPause;

    public static void playNetSound(BmobFile recordFile, final MediaPlayer.OnCompletionListener onCompletionListener) {
        RecordDownloader downloader = new RecordDownloader(recordFile, new RecordDownloader.onDownloadListener() {
            @Override
            public void onComplete(String filePath) {
                playSound(filePath, onCompletionListener);
            }
        });
        downloader.download();


    }
    public static void playSound(String filePath, MediaPlayer.OnCompletionListener onCompletionListener) {

        if (sMediaPlayer == null) {
            sMediaPlayer = new MediaPlayer();
            sMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    sMediaPlayer.reset();
                    return false;
                }
            });
        } else {
            sMediaPlayer.reset();
        }

        //idle状态
        try {
            sMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            sMediaPlayer.setOnCompletionListener(onCompletionListener);
            sMediaPlayer.setDataSource(filePath);
            sMediaPlayer.prepare();
            sMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void pause() {
        if (sMediaPlayer != null && sMediaPlayer.isPlaying()) {
            sMediaPlayer.pause();
            isPause = true;

        }
    }

    public static void resume() {
        if (sMediaPlayer != null && isPause) {
            sMediaPlayer.start();
            isPause = false;

        }
    }

    public static void release() {
        if (sMediaPlayer != null) {
            sMediaPlayer.release();
            sMediaPlayer = null;
        }
    }

    public static void stop() {
        if (sMediaPlayer != null && !isPause) {
            sMediaPlayer.stop();
        }
    }



}
