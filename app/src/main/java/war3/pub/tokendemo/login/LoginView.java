package war3.pub.tokendemo.login;

import com.hannesdorfmann.mosby.mvp.MvpView;

import war3.pub.tokendemo.model.UserInfo;

/**
 * Created by turbo on 2016/11/2.
 */

public interface LoginView extends MvpView {

    public void showError(String errMessage);

    public void showLoading();

    void loginSuccessful(UserInfo user);
}
