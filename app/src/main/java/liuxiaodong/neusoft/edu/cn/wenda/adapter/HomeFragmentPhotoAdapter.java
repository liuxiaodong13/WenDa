package liuxiaodong.neusoft.edu.cn.wenda.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import liuxiaodong.neusoft.edu.cn.wenda.R;
import liuxiaodong.neusoft.edu.cn.wenda.base.BaseActivity;
import liuxiaodong.neusoft.edu.cn.wenda.listener.onPictureItemClickListener;

/**
 * Created by DONG on 2016/10/24.
 */

public class HomeFragmentPhotoAdapter extends RecyclerView.Adapter<HomeFragmentPhotoAdapter.PhotoVH> {

    private final List<String> photos = new ArrayList<>();
    private final BaseActivity activity;

    public HomeFragmentPhotoAdapter(BaseActivity activity, List<String> photos) {
        if (photos != null) {
            setData(photos);
        }
        this.activity = activity;
    }

    public HomeFragmentPhotoAdapter(BaseActivity activity) {
        this.activity = activity;
    }


    @Override
    public int getItemCount() {
        return photos.size();
    }

    public void setData(List<String> photoUris) {
        photos.clear();
        photos.addAll(photoUris);
        notifyDataSetChanged();
    }

    @Override
    public PhotoVH onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.photo_img_item, parent, false);
        return new PhotoVH(itemView);
    }

    @Override
    public void onBindViewHolder(PhotoVH holder, final int position) {
        ImageView photoView = holder.ivPhoto;
        final String url = photos.get(position);
        Glide.with(activity).load(url).thumbnail(.1f).centerCrop().crossFade().diskCacheStrategy(DiskCacheStrategy.RESULT).into(photoView);
//        photoView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Logger.d("点击图片!positoin = " + position);
//                Logger.d("路径:" + url);
//
//            }
//        });

        photoView.setOnClickListener(new onPictureItemClickListener(photos, position, activity));
        CheckBox checkBox = holder.checkbox;


        checkBox.setVisibility(View.GONE);

        checkBox.setTag(position);
        //checkBox.setOnClickListener(checkedChangeListener);
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
