package liuxiaodong.neusoft.edu.cn.wenda.listener.base.relevantlist;

import android.content.Context;

import cn.bmob.v3.exception.BmobException;
import liuxiaodong.neusoft.edu.cn.wenda.event.Event;
import liuxiaodong.neusoft.edu.cn.wenda.event.EventManager;
import liuxiaodong.neusoft.edu.cn.wenda.listener.base.onDeleteLongClickListener;
import liuxiaodong.neusoft.edu.cn.wenda.model.Question;
import liuxiaodong.neusoft.edu.cn.wenda.other.Deleter;
import liuxiaodong.neusoft.edu.cn.wenda.other.QuestionDeleter;
import liuxiaodong.neusoft.edu.cn.wenda.utils.Logger;
import liuxiaodong.neusoft.edu.cn.wenda.utils.T;

/**
 *
 * Created by DONG on 2016/11/13.
 */

public class onQuestionLongClickListener extends onDeleteLongClickListener implements Deleter.onDeleteListener {
    private Question mQuestion;

    public onQuestionLongClickListener(Question question, Context context) {
        super(question.getObjectId(), context, Question.class, QUESTION);
        this.mQuestion = question;
    }

    @Override
    public void doDelete() {

        Deleter deleter = new QuestionDeleter(mQuestion,this);
        deleter.delete();
    }

    @Override
    public void onDeleteSuccess() {
//        EventManager.postSticky(new Event(Event.REFRESH_USER_ANSWER_LIST));
        EventManager.postSticky(new Event(Event.REFRESH_USER_RELEVANT_LIST));
    }


    @Override
    public void onSuccess() {
        T.makeS(context, "删除成功！");
        onDeleteSuccess();
    }

    @Override
    public void onFailed(String[] failUrls, BmobException e) {
        T.makeS(context, "删除失败！");
        Logger.e(String.valueOf(failUrls) + e);

    }

    @Override
    public void onDeleteRecordFailed(BmobException e) {
        T.makeS(context, "删除记录失败！");
        Logger.e(e.toString());

    }
}
