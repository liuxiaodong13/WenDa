package liuxiaodong.neusoft.edu.cn.wenda.listener;

import android.content.Intent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import liuxiaodong.neusoft.edu.cn.wenda.activity.PhotoPreviewActivity;
import liuxiaodong.neusoft.edu.cn.wenda.base.BaseActivity;

/**
 * Created by DONG on 2017/3/22.
 */

public class onPictureItemClickListener implements View.OnClickListener {
    private BaseActivity activity;

    private List<String> photos;
    private int index;

    public onPictureItemClickListener(List<String> photos, int index, BaseActivity activity) {
        this.photos = photos;
        this.index = index;
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {

        Intent intent = new Intent(activity, PhotoPreviewActivity.class);
        intent.putStringArrayListExtra(PhotoPreviewActivity.PHOTOS, (ArrayList<String>) photos);
        intent.putExtra(PhotoPreviewActivity.INDEX, index);
        activity.startActivity(intent);


    }

//    private List<Photo> transform() {
//        List<Photo> photos = new ArrayList<>();
//        for (String photoUrl : this.photos) {
//            Photo photo1 = new Photo();
//            photo1.uri = photoUrl;
//            photo1.isChecked = true;
//            photos.add(photo1);
//        }
//
//        return photos;
//    }


    /**
     *    public void onPhotoListItemClick(View view) {
     fab.hide();
     int index = rvContainer.getChildViewHolder(view).getLayoutPosition();
     FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
     PhotoPreviewFragment f = PhotoPreviewFragment.newInstance(currentDisplayPhotos, index);
     ft.add(R.id.container, f, null);
     ft.addToBackStack(null);
     ft.commit();
     }
     */
}
