package war3.pub.tokendemo.login;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import rx.functions.Action1;
import war3.pub.tokendemo.api.ApiClient;
import war3.pub.tokendemo.api.RxSchedulers;
import war3.pub.tokendemo.model.UserInfo;

/**
 * Created by turbo on 2016/11/2.
 */

public class LoginPresenter extends MvpBasePresenter<LoginView> {
    public void login(String email, String password) {
        ApiClient.getInstance().getLiveApi().login(email, password)
                .compose(RxSchedulers.<UserInfo>applySchedulers())
                .subscribe(new Action1<UserInfo>() {
                    @Override
                    public void call(UserInfo userInfo) {
                        getView().loginSuccessful(userInfo);
                    }
                });
    }
}
