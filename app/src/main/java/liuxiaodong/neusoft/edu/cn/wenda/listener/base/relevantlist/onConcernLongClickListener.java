package liuxiaodong.neusoft.edu.cn.wenda.listener.base.relevantlist;

import android.content.Context;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import liuxiaodong.neusoft.edu.cn.wenda.conn.BmobConn;
import liuxiaodong.neusoft.edu.cn.wenda.event.Event;
import liuxiaodong.neusoft.edu.cn.wenda.event.EventManager;
import liuxiaodong.neusoft.edu.cn.wenda.listener.base.onDeleteLongClickListener;
import liuxiaodong.neusoft.edu.cn.wenda.model.Question;
import liuxiaodong.neusoft.edu.cn.wenda.utils.Logger;
import liuxiaodong.neusoft.edu.cn.wenda.utils.T;

/**
 * Created by DONG on 2016/11/13.
 */

public class onConcernLongClickListener extends onDeleteLongClickListener {

    public onConcernLongClickListener(String objectId, Context context) {
        super(objectId, context, Question.class, CONCERN);
    }


    @Override
    public void doDelete() {
        //
        BmobConn.removeConcernQuestion(objectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    T.makeS(context, "删除成功！");
                    onDeleteSuccess();

                } else {
                    T.makeS(context, "删除失败！");
                    Logger.e(e + "");
                }
            }
        });

    }

    @Override
    public void onDeleteSuccess() {
        EventManager.postSticky(new Event(Event.REFRESH_USER_RELEVANT_LIST));
    }


}
