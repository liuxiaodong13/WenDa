package liuxiaodong.neusoft.edu.cn.wenda.model;

/**
 * Created by DONG on 2017/3/25.
 */

public class Record {
    private String second;
    private String filePath;

    public Record(String second, String filePath) {
        this.second = second;
        this.filePath = filePath;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
