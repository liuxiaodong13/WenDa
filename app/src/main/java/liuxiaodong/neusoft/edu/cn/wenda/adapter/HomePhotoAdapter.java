package liuxiaodong.neusoft.edu.cn.wenda.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import liuxiaodong.neusoft.edu.cn.wenda.utils.Logger;
import liuxiaodong.neusoft.edu.cn.wenda.utils.MyUtils;

/**
 * Created by DONG on 2016/10/24.
 */

public class HomePhotoAdapter extends RecyclerView.Adapter<HomePhotoAdapter.PhotoVH> {

    private final List<String> photos = new ArrayList<>();
    private final Context context;

    public HomePhotoAdapter(Context context, List<String> photos) {
        if (photos != null) {
            this.photos.addAll(photos);
        }
        this.context = context;
    }

    public HomePhotoAdapter(Context context) {
        this.context = context;
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
//        View itemView = LayoutInflater.from(context).inflate(R.layout.photo_img_item, parent, false);
        ImageView imageView = new ImageView(context);
        int widthPixels = MyUtils.getScreenMetrics(context).widthPixels;
        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(widthPixels  / 3, widthPixels / 3);
        imageView.setLayoutParams(params);
        parent.addView(imageView);
        return new PhotoVH(imageView);
    }

    @Override
    public void onBindViewHolder(PhotoVH holder, final int position) {
        ImageView photoView = holder.ivPhoto;
        final String url = photos.get(position);
        Glide.with(context).load(url).thumbnail(.1f).centerCrop().crossFade().diskCacheStrategy(DiskCacheStrategy.RESULT).into(photoView);
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.d("点击图片!positoin = " + position);
                Logger.d("路径:" + url);

            }
        });
//        CheckBox checkBox = holder.checkbox;


//        checkBox.setVisibility(View.GONE);
//
//        checkBox.setTag(position);
        //checkBox.setOnClickListener(checkedChangeListener);
    }



    class PhotoVH extends RecyclerView.ViewHolder {
        ImageView ivPhoto;
//        CheckBox checkbox;

        public PhotoVH(View itemView) {
            super(itemView);
//            ivPhoto = (ImageView) itemView.findViewById(R.id.ivPhoto);
//            checkbox = (CheckBox) itemView.findViewById(R.id.checkbox);
            ivPhoto = (ImageView) itemView;
        }

    }
}
