package liuxiaodong.neusoft.edu.cn.wenda.other;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import liuxiaodong.neusoft.edu.cn.wenda.conn.BmobConn;
import liuxiaodong.neusoft.edu.cn.wenda.model.Answer;
import liuxiaodong.neusoft.edu.cn.wenda.model.AnswerWrapper;
import liuxiaodong.neusoft.edu.cn.wenda.model.MyUser;

/**
 * /根据参数list查出 当前用户是否点赞了里面的answer
 * Created by DONG on 2016/11/5.
 */

public class LikesDataLoader {
    public static final String TAG = "LikesDataLoader";
    private CompleteListener completeListener;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (wrapperMap.size() == answers.size()) {
                //进行排序
                for (int i = 0; i < answers.size(); i++) {
                    Log.i(TAG, "handleMessage: answerWrapper:" + wrapperMap.get(i));
                    wrappers.add(wrapperMap.get(i));
                }

                if (completeListener != null)
                    completeListener.onComplete(wrappers);
            }
        }
    };

    private List<Answer> answers;
    private ArrayList<AnswerWrapper> wrappers = new ArrayList<>();
    private Map<Integer, AnswerWrapper> wrapperMap = new LinkedHashMap<>();


    public LikesDataLoader(List<Answer> answers, CompleteListener l) {
        this.answers = answers;
        this.completeListener = l;
       // load();
    }

    public interface CompleteListener {
        void onComplete(ArrayList<AnswerWrapper> wrappers);
    }

    public void load() {
        for (int i = 0; i < answers.size(); i++) {
            isLike(answers.get(i), i);
        }

    }

    private void isLike(final Answer answer, final int index) {
        BmobConn.queryAll(MyUser.class, new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> list, BmobException e) {
                if (e == null) {
                    boolean isLike = false;
                    for (MyUser user : list) {
                        if (user.getObjectId().equals(getCurrentUser().getObjectId())) {
                            //说明已点赞
                            isLike = true;
                        }
                    }
                    wrapperMap.put(index, new AnswerWrapper(answer, isLike));
                    handler.sendEmptyMessage(0);

                } else {
                    Log.i(TAG, "done: " + e);
                }
            }
        }, new BmobConn.Config() {
            @Override
            public void setArguments(BmobQuery query) {
                Answer answer1 = new Answer();
                answer1.setObjectId(answer.getObjectId());
                query.addWhereRelatedTo("likes", new BmobPointer(answer1));

            }
        });
    }

    private MyUser getCurrentUser() {
        return BmobUser.getCurrentUser(MyUser.class);
    }


}
