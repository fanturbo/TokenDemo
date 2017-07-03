package war3.pub.tokendemo.demo2;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.net.UnknownHostException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;
import war3.pub.tokendemo.R;
import war3.pub.tokendemo.api.ApiClient;
import war3.pub.tokendemo.api.BaseResult;
import war3.pub.tokendemo.api.RxSchedulers;
import war3.pub.tokendemo.base.BaseActivity;
import war3.pub.tokendemo.main.FollowUserAdapter;
import war3.pub.tokendemo.model.CodingUser;
import war3.pub.tokendemo.util.SharedPreferencesUtils;

public class Main2Activity extends BaseActivity {

    @BindView(R.id.tv)
    TextView tv;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_2);
        mContext = this;
        ButterKnife.bind(this);
        getFollowLive();
    }

    private void getFollowLive() {
        ApiClient.getInstance().getLiveApi().getCurrentUser()
                .compose(RxSchedulers.<BaseResult<CodingUser>>applySchedulers())
                .subscribe(new Action1<BaseResult<CodingUser>>() {
                               @Override
                               public void call(BaseResult<CodingUser> codingUser) {
                                    tv.setText(codingUser.getData().getPhone());
                                   Log.i("======", codingUser.getData().getPhone());
                               }
                           }
                        , new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Log.i("======", throwable.toString());
                                if (throwable instanceof UnknownHostException) {
                                    Toast.makeText(mContext, "无网络", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_clear) {
            Snackbar.make(fab, "Clear Cookie", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            SharedPreferencesUtils.saveCookie(Main2Activity.this, null);
            tv.setText("");
            return true;
        } else if (id == R.id.action_refresh) {
            Snackbar.make(fab, "重新请求数据", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            getFollowLive();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
