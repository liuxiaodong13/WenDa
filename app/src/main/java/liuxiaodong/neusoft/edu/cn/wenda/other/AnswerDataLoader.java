package liuxiaodong.neusoft.edu.cn.wenda.other;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import liuxiaodong.neusoft.edu.cn.wenda.conn.BmobConn;
import liuxiaodong.neusoft.edu.cn.wenda.model.Answer;
import liuxiaodong.neusoft.edu.cn.wenda.model.AnswerWrapper;

/**
 * Created by DONG on 2016/11/7.
 */

public class AnswerDataLoader {
    public static final String TAG = "AnswerDataLoader";
    private int pageIndex;
    private int listItems;
    private String questionId;
    private LikesDataLoader.CompleteListener completeListener;
    private BmobConn.Config config;
    private LikesDataLoader likesDataLoader;

    public AnswerDataLoader(int pageIndex, int listItems, String questionId, LikesDataLoader.CompleteListener completeListener, BmobConn.Config config) {
        this.pageIndex = pageIndex;
        this.listItems = listItems;
        this.questionId = questionId;
        this.completeListener = completeListener;
        this.config = config;

    }

    public void load() {
        //查出该条question的所有回答
        BmobConn.queryAll(Answer.class, new FindListener<Answer>() {
            @Override
            public void done(List<Answer> list, BmobException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        completeListener.onComplete(new ArrayList<AnswerWrapper>());
                        return;
                    }
                    //根据参数list查出 当前用户是否点赞了里面的answer

                    likesDataLoader = new LikesDataLoader(list, completeListener);
                    likesDataLoader.load();

                    Log.i(TAG, "done: 当前问题的所有回答:" + list.size());
                } else {
                    Log.i(TAG, "done: " + e);
                }
            }
        }, config);
    }
}
