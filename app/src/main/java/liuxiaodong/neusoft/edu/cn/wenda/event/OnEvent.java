package liuxiaodong.neusoft.edu.cn.wenda.event;

import org.greenrobot.eventbus.Subscribe;

import liuxiaodong.neusoft.edu.cn.wenda.utils.Logger;

/**
 * Created by DONG on 2017/3/14.
 */

public class OnEvent {
    private IReceiver receiver;

    public OnEvent(IReceiver receiver) {
        //EventManager.register(this);
        this.receiver = receiver;
    }

    @Subscribe
    public void onEvent(Event event) {
        Logger.d("onEvent!" + receiver);
        if (receiver != null) {

            receiver.onReceive(event);
        }
    }

    @Subscribe(sticky = true)
    public void onStickyEvent(Event stickyEvent) {
        Logger.d("onStickyEvent!" + receiver);
        if (receiver != null) {
            receiver.onStickyReceive(stickyEvent);
            EventManager.removeAllStickyEvents();
        }
    }


    public interface IReceiver {
        void onReceive(Event event);
        void onStickyReceive(Event stickyEvent);

    }


}
