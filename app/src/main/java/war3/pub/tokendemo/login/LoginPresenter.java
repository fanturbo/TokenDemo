package war3.pub.tokendemo.login;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import rx.Subscriber;
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
                .subscribe(new Subscriber<UserInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().showError("登录失败了！");
                    }

                    @Override
                    public void onNext(UserInfo userInfo) {
                        getView().loginSuccessful(userInfo);
                    }
                });
    }
}
