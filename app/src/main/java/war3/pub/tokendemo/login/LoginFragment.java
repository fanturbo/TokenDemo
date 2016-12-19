package war3.pub.tokendemo.login;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import war3.pub.tokendemo.R;
import war3.pub.tokendemo.base.BaseFragment;
import war3.pub.tokendemo.main.MainActivity;
import war3.pub.tokendemo.model.UserInfo;
import war3.pub.tokendemo.util.SharedPreferencesUtils;

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
    @BindView(R.id.btn_register)
    Button mRegisterButton;
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
        ((LoginPresenter) presenter).login(mUserName.getText().toString().trim(), mPasswordView.getText().toString().trim());
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
    public void loginSuccessful(UserInfo user) {
        mProgressDialog.dismiss();
        SharedPreferencesUtils.saveToken(mContext, user.getData().getToken());
        getActivity().startActivity(new Intent(mContext, MainActivity.class));
    }
}
