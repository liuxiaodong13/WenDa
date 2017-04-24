package liuxiaodong.neusoft.edu.cn.wenda.model;

import liuxiaodong.neusoft.edu.cn.wenda.R;

/**
 * Created by DONG on 2016/10/28.
 */

public class CategoryTag {
    private Category category;
    private int categoryImageResId;
    private int OTHERS = R.drawable.ic_others;

    public CategoryTag(Category category) {
        this.category = category;
        initCategoryImageResId();
    }
    private void initCategoryImageResId() {
        if (category == null) {
            throw new RuntimeException("can not get category image ResId the category field is null!!!");
        } else {
            String name = category.getName();
            if (name.equals("Android")) {
                categoryImageResId = R.drawable.ic_android;
            }else if (name.equals("IOS")) {
                categoryImageResId = R.drawable.ic_ios;
            }else if (name.equals("PHP")) {
                categoryImageResId = R.drawable.ic_php;
            }else {
                categoryImageResId = OTHERS;
            }
        }
    }

    public int getCategoryImageResId() {
        return categoryImageResId;
    }
}
