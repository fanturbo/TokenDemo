package war3.pub.tokendemo.api;

/**
 * Created by turbo on 2017/3/14.
 */

public class BaseResult<T> {
    public static final int SUCCESS = 200; // 成功
    protected int code;
    protected T data;
    protected MessageBean msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public MessageBean getMsg() {
        return msg;
    }

    public void setMsg(MessageBean msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static class MessageBean {

        /**
         * user_not_login : 用户未登录
         */

        private String user_not_login;

        public String getUser_not_login() {
            return user_not_login;
        }

        public void setUser_not_login(String user_not_login) {
            this.user_not_login = user_not_login;
        }
    }
}
