package liuxiaodong.neusoft.edu.cn.wenda.other;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import liuxiaodong.neusoft.edu.cn.wenda.conn.BmobConn;
import liuxiaodong.neusoft.edu.cn.wenda.model.Answer;
import liuxiaodong.neusoft.edu.cn.wenda.model.AnswerWrapper;
import liuxiaodong.neusoft.edu.cn.wenda.model.MyUser;
import liuxiaodong.neusoft.edu.cn.wenda.model.Question;

import static cn.bmob.v3.BmobUser.getCurrentUser;

/**
 * Created by DONG on 2016/11/12.
 */

public class AnswerWrapperLoader {
    public static final String TAG = "AnswerWrapperLoader";
    private String questionId;
    private int listItems;
    private int finalSize;
    private int pageIndex = 1;
    private onCompleteListener listener;
    private ArrayList<AnswerWrapper> wrappers = new ArrayList<>();
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "handleMessage: 收到消息：此时wrapperSize和finalSize分别为" +wrappers.size() + "  " + finalSize );
            if (wrappers.size() == finalSize) {
                callBack();
            }
        }
    };

    public AnswerWrapperLoader(String questionId, int listItems, onCompleteListener l) {
        this.questionId = questionId;
        this.listItems = listItems;
        this.listener = l;
        load();
    }

    private void load() {
        wrappers.clear();
        BmobConn.queryAll(Answer.class, new FindListener<Answer>() {
            @Override
            public void done(List<Answer> list, BmobException e) {
                if (e == null) {
                    Log.i(TAG, "done: List<Answer>Size:" + list.size());
                    //查出所有的answer  再查当前用户是否分别点赞了里面的answer
                    if (list.size() == 0) {

                        callBack();
                        return;
                    }
                    finalSize = list.size();
                    for (int i = 0; i < list.size(); i++) {

                        wrappers.add(i, new AnswerWrapper(list.get(i), false));
                        isLike(list.get(i), i);
                    }


                } else {
                    Log.i(TAG, "done: " + e);
                }

            }
        }, new BmobConn.Config() {
            @Override
            public void setArguments(BmobQuery query) {
                Log.i(TAG, "setArguments: 设置查询条件。当前跳过前" + (pageIndex - 1) * listItems + "条数据");
                query.include("author");
                Question question = new Question();
                question.setObjectId(questionId);
                query.setSkip((pageIndex - 1) * listItems);
                query.setLimit(listItems);
                query.order("-createdAt");
                query.addWhereEqualTo("question", new BmobPointer(question));
            }
        });

    }

    private void isLike(final Answer answer, final int index) {
        BmobConn.queryAll(MyUser.class, new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> list, BmobException e) {
                if (e == null) {
                    for (MyUser user : list) {
                        //answerWrappers[index] = new AnswerWrapper(answer, false);
                        if (user.getObjectId().equals(getCurrentUser().getObjectId())) {
                            //说明已点赞
                            //answerWrappers[index].setLike(true);
                            Answer answer1 = wrappers.get(index).getAnswer();
                            Log.i(TAG, "done: answer:" + answer + "index:" + index);
                            wrappers.set(index, new AnswerWrapper(answer1, true));
                            break;

                        }
                    }

                    handler.sendEmptyMessage(0);
                    Log.i(TAG, "done: 点赞查询成功！sendEmptyMessage wrappers:" + wrappers);

                } else {
                    Log.i(TAG, "done: " + e);
                }
            }
        },   new BmobConn.Config() {
            @Override
            public void setArguments(BmobQuery query) {
                Answer answer1 = new Answer();
                answer1.setObjectId(answer.getObjectId());
                query.addWhereRelatedTo("likes", new BmobPointer(answer1));

            }
        });
    }

    public void loadMore() {
        pageIndex++;
        load();

    }

    public void reFresh() {
        pageIndex = 1;
        load();
    }

    private void callBack() {
        if (listener != null) {
            if (pageIndex > 1) {
                listener.onLoadingMoreComplete(wrappers);
                Log.i(TAG, "callBack: onLoadingMoreComplete");
            } else {
                listener.onLoadComplete(wrappers);
                Log.i(TAG, "callBack: onLoadComplete");
            }
        }
    }

    public interface onCompleteListener {
        void onLoadComplete(ArrayList<AnswerWrapper> wrappers);

        void onLoadingMoreComplete(ArrayList<AnswerWrapper> wrappers);
    }

}
