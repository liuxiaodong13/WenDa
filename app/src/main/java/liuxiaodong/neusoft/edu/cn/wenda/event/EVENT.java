package liuxiaodong.neusoft.edu.cn.wenda.event;

/**
 * Created by DONG on 2017/3/11.
 */

public class Event {
    public static final String LOGIN_SUCCESS =  "0";
    public static final String LOGOUT_SUCCESS = "1";
    public static final String REFRESH_QUESTION_LIST = "3";


    public static final String DELETE_DRAFT = "4";
    public static final String REFRESH_NOTIFICATIONS =  "5";

    public static final String DELETE_DRAFT_AUTO = "6";
    public static final String REFRESH_DRAFTS = "7";

    public static final String REFRESH_USER_ANSWER_LIST = "8";

    public static final String REFRESH_USER_RELEVANT_LIST = "9";

    public static final String REFRESH_ANSWER_LIST = "10";
    public static final String REFRESH_CONCERN_LIST = "11";

    public static final String REFRESH_USER_INFO = "12";

    public static final String CLEAN_CACHE_SUCCESS = "13";

    public static final String REFRESH_USERQS_LIST = "14";

    public static final String REFRESH_USER_CONCERN_LIST = "15";


    public final String message;

    public Event(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object obj) {
        Event e = (Event) obj;
        return this.message.equals(e);

    }

    @Override
    public String toString() {
        return "Event{" +
                "message='" + message + '\'' +
                '}';
    }
}
