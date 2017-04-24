package liuxiaodong.neusoft.edu.cn.wenda.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import liuxiaodong.neusoft.edu.cn.wenda.R;
import liuxiaodong.neusoft.edu.cn.wenda.fragment.PhotoPreviewFragment;
import liuxiaodong.neusoft.edu.cn.wenda.model.Photo;

/**
 * Created by DONG on 2016/10/24.
 */

public class PhotoPreviewActivity extends AppCompatActivity {
    public static final String INDEX = "index";
    public static final String PHOTOS = "photos";
    private int index;

    @BindView(R.id.container)
    FrameLayout frameLayout;
    private PhotoPreviewFragment previewFragment;
    private ArrayList<String> photoUrls;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getExtraData();
        setContentView(R.layout.activity_preview_photo);
        ButterKnife.bind(this);
        initFragment();
    }

    private void getExtraData() {

        Intent intent = getIntent();
        photoUrls = intent.getStringArrayListExtra(PHOTOS);
        index = intent.getIntExtra(INDEX, 0);

    }

    private void initFragment() {
        previewFragment = PhotoPreviewFragment.newInstance(transform(), index);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, previewFragment, null);
        ft.addToBackStack(null);
        ft.commit();
    }

    private List<Photo> transform() {
        List<Photo> photos = new ArrayList<>();
        for (String photoUrl : photoUrls) {
            Photo photo1 = new Photo();
            photo1.uri = photoUrl;
            photo1.isChecked = true;
            photos.add(photo1);
        }

        return photos;
    }

    @Override
    public void onBackPressed() {
       finish();

    }
}
