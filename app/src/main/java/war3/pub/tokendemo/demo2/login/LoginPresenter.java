package war3.pub.tokendemo.demo2.login;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import retrofit2.Response;
import rx.Subscriber;
import war3.pub.tokendemo.api.ApiClient;
import war3.pub.tokendemo.api.BaseResult;
import war3.pub.tokendemo.api.RxSchedulers;
import war3.pub.tokendemo.model.UserInfo;

/**
 * Created by turbo on 2016/11/2.
 */

public class LoginPresenter extends MvpBasePresenter<LoginView> {
    public void login(String email, String password) {
        ApiClient.getInstance().getLiveApi().loginCoding(email, password)
                .compose(RxSchedulers.<Response<BaseResult>>applySchedulers())
                .subscribe(new Subscriber<Response<BaseResult>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().showError("登录失败了！");
                    }

                    @Override
                    public void onNext(Response<BaseResult> userInfo) {
                        getView().loginSuccessful(userInfo);
                    }
                });
    }
}
