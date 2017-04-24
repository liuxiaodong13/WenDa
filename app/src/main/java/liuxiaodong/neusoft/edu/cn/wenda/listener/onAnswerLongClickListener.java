package liuxiaodong.neusoft.edu.cn.wenda.listener;

import android.content.Context;

import cn.bmob.v3.exception.BmobException;
import liuxiaodong.neusoft.edu.cn.wenda.event.Event;
import liuxiaodong.neusoft.edu.cn.wenda.event.EventManager;
import liuxiaodong.neusoft.edu.cn.wenda.listener.base.onDeleteLongClickListener;
import liuxiaodong.neusoft.edu.cn.wenda.model.Answer;
import liuxiaodong.neusoft.edu.cn.wenda.other.AnswerDeleter;
import liuxiaodong.neusoft.edu.cn.wenda.other.Deleter;
import liuxiaodong.neusoft.edu.cn.wenda.utils.Logger;
import liuxiaodong.neusoft.edu.cn.wenda.utils.T;

/**
 * 我的回答列表中 点击进行
 * Created by DONG on 2016/11/13.
 */

public class onAnswerLongClickListener extends onDeleteLongClickListener implements Deleter.onDeleteListener {


    private final Answer answer;

    public onAnswerLongClickListener(Answer answer, Context context) {
        super(answer.getObjectId(), context, Answer.class, ANSWER);
        this.answer = answer;
    }

    @Override
    public void doDelete() {
        AnswerDeleter  deleter = new AnswerDeleter(answer, this);
        deleter.delete();

    }

    @Override
    public void onDeleteSuccess() {
        EventManager.postSticky(new Event(Event.REFRESH_USER_ANSWER_LIST));
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
