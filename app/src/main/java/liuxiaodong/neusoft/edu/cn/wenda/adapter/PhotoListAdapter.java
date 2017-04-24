package liuxiaodong.neusoft.edu.cn.wenda.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import liuxiaodong.neusoft.edu.cn.wenda.R;
import liuxiaodong.neusoft.edu.cn.wenda.model.Photo;

/**
 * Created by DONG on 2016/10/23.
 */

public class PhotoListAdapter extends RecyclerView.Adapter<PhotoListAdapter.PhotoVH> {

    private final List<Photo> photos = new ArrayList<>();
    private final Activity activity;
    private onPhotoPreviewListChangedListener listChangedListener;


    public interface onPhotoPreviewListChangedListener {
        void onChanged(List<Photo> photos);
    }
    public PhotoListAdapter(Activity activity, List<Photo> photos, onPhotoPreviewListChangedListener l) {
        if (photos != null) {
            this.photos.addAll(photos);
        }

        this.activity = activity;
        this.listChangedListener = l;
    }
    public PhotoListAdapter(Activity activity, List<Photo> photos) {
        if (photos != null) {
            this.photos.addAll(photos);
        }
        this.activity = activity;
    }



    @Override
    public int getItemCount() {
        return photos.size();
    }

    public void setData(List<Photo> photoUris) {
        photos.clear();
        photos.addAll(photoUris);
        this.notifyDataSetChanged();
    }

    @Override
    public PhotoVH onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.photo_img_item, parent, false);
        return new PhotoVH(itemView);
    }

    @Override
    public void onBindViewHolder(PhotoVH holder, int position) {
        ImageView photoView = holder.ivPhoto;
        Photo photo = photos.get(position);
        Glide.with(activity).load(photo.uri).thumbnail(.1f).centerCrop().crossFade().diskCacheStrategy(DiskCacheStrategy.RESULT).into(photoView);
        CheckBox checkBox = holder.checkbox;
        checkBox.setVisibility(View.GONE);
        photoView.setOnClickListener(new MyOnClickListener(position));
    }

    private class MyOnClickListener implements View.OnClickListener {

        private int position;
        MyOnClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            showDeleteMenu(v);
        }

        private void showDeleteMenu(View v) {
            PopupMenu menu = new PopupMenu(activity, v);
            menu.getMenuInflater().inflate(R.menu.delete, menu.getMenu());
            menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.delete) {
                        photos.remove(position);
                        PhotoListAdapter.this.notifyDataSetChanged();

                        //这个如果设置了选择图片预览更改的监听器 触发 并更新activity中持有的photos对象
                        if (listChangedListener != null) {
                            listChangedListener.onChanged(photos);

                        }

                    }
                    return true;
                }
            });

            menu.show();

        }
    }

    class PhotoVH extends RecyclerView.ViewHolder {
        ImageView ivPhoto;
        CheckBox checkbox;

        public PhotoVH(View itemView) {
            super(itemView);
            ivPhoto = (ImageView) itemView.findViewById(R.id.ivPhoto);
            checkbox = (CheckBox) itemView.findViewById(R.id.checkbox);
        }

    }

}
