package war3.pub.tokendemo;

import android.app.Application;
import android.content.Context;

/**
 * Created by turbo on 2017/7/3.
 */

public class App extends Application {
    private static Context mContext;

    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    // 获得全局上下文
    public static Context getContext() {
        return mContext;
    }
}
