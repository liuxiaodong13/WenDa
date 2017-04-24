package liuxiaodong.neusoft.edu.cn.wenda.listener;

import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;

import liuxiaodong.neusoft.edu.cn.wenda.base.APP;
import liuxiaodong.neusoft.edu.cn.wenda.event.Event;
import liuxiaodong.neusoft.edu.cn.wenda.event.EventManager;
import liuxiaodong.neusoft.edu.cn.wenda.listener.base.OnItemDeleteClickListener;

/**
 * Created by DONG on 2017/1/18.
 */

public class onDraftDeleteOnClick extends OnItemDeleteClickListener {
    private Integer id;
    private LocalBroadcastManager manager;

    public onDraftDeleteOnClick(String title, Context context) {
        super(title, context);
    }

    public onDraftDeleteOnClick(String title, Context context, Integer id) {
        super(title, context);
        this.id = id;

    }

    @Override
    protected void doDelete() {

        int result = APP.getInstance().getSqLiteHelper().deleteDraft(id);
        if (result >= 1)
            EventManager.postSticky(new Event(Event.REFRESH_DRAFTS));


    }

    @Override
    protected void doCancel() {

    }


}
