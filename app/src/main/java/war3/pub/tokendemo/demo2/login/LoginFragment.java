package war3.pub.tokendemo.demo2.login;


import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Response;
import war3.pub.tokendemo.R;
import war3.pub.tokendemo.api.BaseResult;
import war3.pub.tokendemo.base.BaseFragment;
import war3.pub.tokendemo.demo2.Main2Activity;
import war3.pub.tokendemo.main.MainActivity;
import war3.pub.tokendemo.model.UserInfo;
import war3.pub.tokendemo.util.SharedPreferencesUtils;
import war3.pub.tokendemo.util.SimpleSHA1;

/**
 * 登录界面
 */
public class LoginFragment extends BaseFragment<LoginView, LoginPresenter> implements LoginView {

    @BindView(R.id.auto_ctv_username)
    AutoCompleteTextView mUserName;
    @BindView(R.id.auto_ctv_password)
    EditText mPasswordView;
    @BindView(R.id.login_progress)
    ProgressBar mProgressView;
    @BindView(R.id.login_form)
    ScrollView mLoginFormView;
    @BindView(R.id.btn_login)
    Button mLoginButton;
    @BindView(R.id.tv_error)
    TextView tvError;
    private ProgressDialog mProgressDialog;

    @Override
    public LoginPresenter createPresenter() {
        return new LoginPresenter();
    }


    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_login;
    }

    @OnClick(R.id.btn_login)
    void register() {
        showLoading();
        SharedPreferencesUtils.saveUserName(mContext, mUserName.getText().toString().trim());
        SharedPreferencesUtils.savePassword(mContext, mPasswordView.getText().toString().trim());
        ((LoginPresenter) presenter).login(mUserName.getText().toString().trim(), SimpleSHA1.sha1(mPasswordView.getText().toString().trim()));
    }

    @Override
    public void showError(String errMessage) {
        mProgressDialog.dismiss();
        tvError.setText(errMessage + "");
    }

    @Override
    public void showLoading() {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCanceledOnTouchOutside(true);
        mProgressDialog.setMessage("拼命加载中...");
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    @Override
    public void loginSuccessful(Response<BaseResult> response) {
        mProgressDialog.dismiss();
        if (response != null && response.body().getCode() == 0) {
            List<String> cookies = response.headers().values("Set-Cookie");//获取所有cookies
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < cookies.size(); i++) {
                if (!(i == cookies.size())) {
                    sb.append(cookies.get(i) + ";");
                } else {
                    sb.append(cookies.get(i));
                }
            }
            SharedPreferencesUtils.saveCookie(mContext, sb.toString());
            getActivity().startActivity(new Intent(mContext, Main2Activity.class));
        } else {
            tvError.setText("登录失败");
        }
    }
}
