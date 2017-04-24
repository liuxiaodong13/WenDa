package liuxiaodong.neusoft.edu.cn.wenda.model;

/**
 * Created by DONG on 2016/11/10.
 */

public class CategoryCheckBoxWrapper {
    private Category category;
    private boolean isCheck;

    public CategoryCheckBoxWrapper(Category category, boolean isCheck) {
        this.category = category;
        this.isCheck = isCheck;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }
}
