package liuxiaodong.neusoft.edu.cn.wenda.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import liuxiaodong.neusoft.edu.cn.wenda.fragment.NotificationFragment;
import liuxiaodong.neusoft.edu.cn.wenda.listener.onNotificationClickListener;
import liuxiaodong.neusoft.edu.cn.wenda.listener.onNotificationLongClickListener;
import liuxiaodong.neusoft.edu.cn.wenda.model.Notification;
import liuxiaodong.neusoft.edu.cn.wenda.ui.NotificationView;

/**
 * Created by DONG on 2017/1/12.
 */

public class NotificationFragmentAdapter extends RecyclerView.Adapter<NotificationFragmentAdapter.NotificationViewHolder> {
    private ArrayList<Notification> notifications = new ArrayList<>();
    private NotificationFragment fragment;

    public NotificationFragmentAdapter(NotificationFragment fragment) {
        this.fragment = fragment;
    }

    public void setData(List<Notification> data) {
        notifications.clear();
        notifications.addAll(data);
        this.notifyDataSetChanged();
    }

    public void addData(List<Notification> data) {
        if (data != null) {
            notifications.addAll(data);
            this.notifyDataSetChanged();
        }


    }

    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        NotificationView notificationView = new NotificationView(context);
        return new NotificationViewHolder(notificationView);
    }

    @Override
    public void onBindViewHolder(NotificationViewHolder holder, int position) {

        holder.notificationView.setTitle(notifications.get(position).getTypeDesc());
        holder.notificationView.setContent(notifications.get(position).getContent());
        holder.notificationView.setRead(notifications.get(position).getRead());
        holder.notificationView.setOnClickListener(new onNotificationClickListener(
                notifications.get(position).getExtra(), notifications.get(position).getObjectId(),fragment.getActivity()));
        holder.notificationView.setOnLongClickListener(new onNotificationLongClickListener(notifications.get(position).getExtra(), notifications.get(position).getObjectId(),
                fragment.getActivity()));
    }


    @Override
    public int getItemCount() {
        return notifications.size();
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder {
        NotificationView notificationView;


        public NotificationViewHolder(View itemView) {
            super(itemView);
            notificationView = (NotificationView) itemView;

        }
    }
}
