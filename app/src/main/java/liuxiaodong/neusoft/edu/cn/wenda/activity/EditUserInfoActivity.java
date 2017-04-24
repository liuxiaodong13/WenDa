package liuxiaodong.neusoft.edu.cn.wenda.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;
import liuxiaodong.neusoft.edu.cn.wenda.R;
import liuxiaodong.neusoft.edu.cn.wenda.base.APP;
import liuxiaodong.neusoft.edu.cn.wenda.base.OnEventBackNavigationActivity;
import liuxiaodong.neusoft.edu.cn.wenda.conn.BmobConn;
import liuxiaodong.neusoft.edu.cn.wenda.event.Event;
import liuxiaodong.neusoft.edu.cn.wenda.event.EventManager;
import liuxiaodong.neusoft.edu.cn.wenda.model.Category;
import liuxiaodong.neusoft.edu.cn.wenda.model.CategoryCheckBoxWrapper;
import liuxiaodong.neusoft.edu.cn.wenda.model.MyUser;
import liuxiaodong.neusoft.edu.cn.wenda.utils.Logger;
import liuxiaodong.neusoft.edu.cn.wenda.utils.T;

/**
 * Created by DONG on 2016/11/8.
 */

public class EditUserInfoActivity extends OnEventBackNavigationActivity {
    public static final String TAG = "EditUserInfoActivity";
    private final int SELECT_USER_ICON = 100;
    private final int SELECT_BACKGROUND = 101;
    @BindView(R.id.iv_user_background)
    ImageView ivUserBackground;
    @BindView(R.id.civ_userIcon)
    CircleImageView civUserIcon;
    @BindView(R.id.et_nick)
    EditText etNick;
    @BindView(R.id.til_nick)
    TextInputLayout tilNick;
    @BindView(R.id.sp_gender)
    Spinner spGender;
    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.til_desc)
    TextInputLayout tilDesc;
    @BindView(R.id.et_location)
    EditText etLocation;
    @BindView(R.id.til_location)
    TextInputLayout tilLocation;
    @BindView(R.id.tv_show_good_at_info)
    TextView tvShowGoodAtInfo;
    private boolean gender = true;
    private ArrayList<String> categories = new ArrayList<>();
    private ArrayList<String> selectedCategories = new ArrayList<>();
    private boolean[] isCheck;
    private HashSet<Integer> multiChoiceIndex;
    private ArrayList<Category> categoryArrayList = new ArrayList<>();
    private BmobFile newUserIcon;
    private BmobFile newUserBackgroundIcon;
    private ArrayList<CategoryCheckBoxWrapper> wrappers = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildActivity(R.layout.activity_edit_user_info);

    }

    @Override
    public void onEvent(Event event) {
        if (event.equals(Event.REFRESH_USER_INFO))
        {
            Logger.d("收到事件：更新user信息!");
            initUserInfo();
            EventManager.removeAllStickyEvents();
        }
    }

    @Override
    protected void initToolbar() {
        initTitle("修改资料");
    }

    @Override
    protected void initView() {
        initGenderSpinner();
    }

    private void initGenderSpinner() {
        String[] gender = {"男", "女"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, gender);
        spGender.setAdapter(arrayAdapter);
        spGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                EditUserInfoActivity.this.gender = position == 0;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    @Override
    protected void initData() {
        initUserInfo();

        BmobConn.queryCategories(new FindListener<Category>() {
            @Override
            public void done(List<Category> list, BmobException e) {
                if (e == null) {
                    categoryArrayList.clear();
                    categoryArrayList.addAll(list);
                    categories.clear();
                    for (Category category : list) {
                        categories.add(category.getName());
                    }
                } else {
                    Log.i(TAG, "done: " + e);
                }

            }

        });

        //查询已选择的 category
        BmobConn.queryAll(Category.class, new FindListener<Category>() {
            @Override
            public void done(List<Category> list, BmobException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        setSelectedCategories(list);
                        StringBuilder builder = new StringBuilder();
                        for (Category category : list) {
                            builder.append(category.getName());
                            builder.append(" ");
                        }
                        tvShowGoodAtInfo.setText(builder.toString());
                    }

                    initCategoryWrapperList(list);
                } else {
                    Log.i(TAG, "done: " + e);
                }

            }

            private void setSelectedCategories(List<Category> list) {
                for (Category category : list) {
                    selectedCategories.clear();
                    selectedCategories.add(category.getName());
                }
            }
        }, new BmobConn.Config() {
            @Override
            public void setArguments(BmobQuery query) {
                query.addWhereRelatedTo("good_at", new BmobPointer(getCurrentUser()));
            }
        });


    }

    private void initUserInfo() {
        BmobConn.queryObject(MyUser.class, getCurrentUser().getObjectId(), new QueryListener<MyUser>() {
            @Override
            public void done(MyUser myUser, BmobException e) {
                bindData(myUser);
            }
        });
    }

    private void initCategoryWrapperList(List<Category> list) {
        wrappers.clear();
        if (list == null) {
            //当前用户还没选择擅长
            for (Category category : categoryArrayList) {
                wrappers.add(new CategoryCheckBoxWrapper(category, false));
            }
        } else {
            for (Category category : categoryArrayList) {
                wrappers.add(new CategoryCheckBoxWrapper(category, list.contains(category)));
            }
        }


    }

    private void bindData(MyUser myUser) {
        spGender.setSelection(myUser.getGender() ? 0 : 1);

        tilNick.getEditText().setText(myUser.getNick());
        tilDesc.getEditText().setText(myUser.getDesc());
        tilLocation.getEditText().setText(myUser.getLocation());
        Glide.with(activity).load(myUser.getUserIcon().getFileUrl()).diskCacheStrategy(DiskCacheStrategy.RESULT).into(civUserIcon);
        if (myUser.getBackground() != null) {
            Glide.with(activity).load(myUser.getBackground().getFileUrl()).diskCacheStrategy(DiskCacheStrategy.RESULT).into(ivUserBackground);
        }

    }

    @OnClick({R.id.iv_user_background, R.id.civ_userIcon, R.id.tv_show_good_at_info, R.id.btn_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_user_background:
                showSelectPhotoActivity(SELECT_BACKGROUND);
                break;
            case R.id.civ_userIcon:
                showSelectPhotoActivity(SELECT_USER_ICON);
                break;
            case R.id.tv_show_good_at_info:
                showSelectDialog();
                break;
            case R.id.btn_confirm:
                //提交更新的资料
                upLoadUserInfo();

                break;
        }
    }

    private void upLoadUserInfo() {
        MyUser user = new MyUser();
        user.setObjectId(getCurrentUser().getObjectId());

        user.setLocation(tilLocation.getEditText().getText().toString());
        user.setNick(tilNick.getEditText().getText().toString());
        user.setDesc(tilDesc.getEditText().getText().toString());
        user.setGender(gender);
        /*if (multiChoiceIndex != null) {
            BmobRelation relation = new BmobRelation();
            Iterator<Integer> iterator = multiChoiceIndex.iterator();
            for (int i = 0; i < categories.size(); i++) {
                while (iterator.hasNext()) {
                    if (iterator.next().equals(i)) {
                        relation.add(categoryArrayList.get(i));
                    }
                }

            }

            user.setGood_at(relation);
        }*/
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    T.makeS(activity, "更新成功！");
                    APP.destroyActivity();
                    showActivity(MainActivity.class);
                } else {
                    T.makeS(activity, "更新失败！");
                    Log.i(TAG, "done: " + e);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String result = data.getStringExtra(PhotoPickerActivity.SELECT_RESULTS);
            if (result == null || result.equals("")) {
                return;
            }
            switch (requestCode) {
                case SELECT_USER_ICON:
                    newUserIcon = new BmobFile(new File(result));
                    upLoadUserIcon();
                    break;
                case SELECT_BACKGROUND:
                    newUserBackgroundIcon = new BmobFile(new File(result));
                    uploadUserBackground();
                    break;
                default:
                    break;
            }
        }

    }

    private void uploadUserBackground() {
        newUserBackgroundIcon.upload(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    T.makeS(activity, "上传背景图片成功！");
                    MyUser user = new MyUser();
                    user.setObjectId(getCurrentUser().getObjectId());
                    user.setBackground(newUserBackgroundIcon);
                    user.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                sendEvent();
                                T.makeS(activity, "更新背景成功！");
                            } else {
                                Log.i(TAG, "done: " + e);
                                T.makeS(activity, "更新背景失败");
                            }
                        }
                    });
                } else {
                    Log.i(TAG, "done: " + e);
                    T.makeS(activity, "上传背景图片失败！");
                }
            }
        });

    }

    private void upLoadUserIcon() {
        newUserIcon.upload(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    T.makeS(activity, "上传头像成功！");
                    MyUser user = new MyUser();
                    user.setObjectId(getCurrentUser().getObjectId());
                    user.setUserIcon(newUserIcon);
                    user.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                sendEvent();
                                T.makeS(activity, "更新头像成功！");

                            } else {
                                Log.i(TAG, "done: " + e);
                                T.makeS(activity, "更新头像失败！");
                            }

                        }
                    });
                } else {
                    T.makeS(activity, "上传头像失败！");
                    Log.i(TAG, "done: " + e);
                }
            }

        });
    }

    private void sendEvent() {
        EventManager.postSticky(new Event(Event.REFRESH_USER_INFO));
    }


    private void showSelectPhotoActivity(int requestCode) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(this, PhotoPickerActivity.class);
        bundle.putBoolean(PhotoPickerActivity.IS_MULTI_SELECT, false);
        bundle.putBoolean(PhotoPickerActivity.IS_SHOW_GIF, false);
        intent.putExtras(bundle);
        startActivityForResult(intent, requestCode);
    }

    private void showSelectDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("请选择");
        final String[] categories = this.categories.toArray(new String[this.categories.size()]);

        isCheck = new boolean[wrappers.size()];
        initIsCheckArray();
        multiChoiceIndex = new HashSet<>();

        builder.setMultiChoiceItems(categories, isCheck, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                updateGoodAt(which, isChecked);
            }

            private void updateGoodAt(final int which, final boolean isChecked) {
                BmobRelation relation = new BmobRelation();
                MyUser user = new MyUser();
                user.setObjectId(getCurrentUser().getObjectId());

                if (isChecked) {
                    relation.add(wrappers.get(which).getCategory());

                } else {
                    relation.remove(wrappers.get(which).getCategory());
                }
                user.setGood_at(relation);
                user.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            wrappers.get(which).setCheck(isChecked);
                            T.makeS(activity, "更新成功！");
                        } else {
                            Log.i(TAG, "done: " + e);
                            T.makeS(activity, "更新失败!");
                        }

                    }
                });
            }
        });

        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringBuilder builder1 = new StringBuilder();
                //Iterator<Integer> iterator = multiChoiceIndex.iterator();
               /* Logger.i(TAG, "onClick: 选择的擅长category:" + multiChoiceIndex.toString());
                for (int i = 0; i < categories.length; i++) {
                    while (iterator.hasNext()) {
                        if (iterator.next().equals(i)) {
                            builder1.append(categories[i]);
                        }

                    }
                }
*/
                for (CategoryCheckBoxWrapper wrapper : wrappers) {
                    if (wrapper.isCheck()) {
                        builder1.append(wrapper.getCategory().getName());
                        builder1.append("");
                    }
                }
                tvShowGoodAtInfo.setText(builder1.toString());
            }
        });

        builder.create().show();

    }

    private void initIsCheckArray() {
        /*for (int i = 0; i < categories.size(); i++) {
            for (int j = 0; j < selectedCategories.size(); j++) {
                if (categories.get(i).equals(selectedCategories.get(j))) {
                    isCheck[i] = true;
                }
            }
        }*/

        //根据wrapperlist 生成 checkarray
        for (int i = 0; i < wrappers.size(); i++) {
            isCheck[i] = wrappers.get(i).isCheck();
        }

    }


}
