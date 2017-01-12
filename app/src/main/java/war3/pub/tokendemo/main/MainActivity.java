package war3.pub.tokendemo.main;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observables.BlockingObservable;
import war3.pub.tokendemo.R;
import war3.pub.tokendemo.api.ApiClient;
import war3.pub.tokendemo.api.RxSchedulers;
import war3.pub.tokendemo.base.BaseActivity;
import war3.pub.tokendemo.model.FollowLive;
import war3.pub.tokendemo.model.UserInfo;
import war3.pub.tokendemo.util.SharedPreferencesUtils;

public class MainActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerview_follow)
    RecyclerView recyclerviewFollow;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    private Context mContext;
    private FollowUserAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        recyclerviewFollow.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        getFollowLive();
    }

    private void getFollowLive() {
        swipeRefreshLayout.setRefreshing(true);
        Observable
                .defer(new Func0<Observable<FollowLive>>() {
                    @Override
                    public Observable<FollowLive> call() {
                        Log.i("======", "请求follow数据");
                        return ApiClient.getInstance().getLiveApi().getFollow(SharedPreferencesUtils.getToken(mContext));
                    }
                })
                .retryWhen(new RetryWithDelay(3, 1))
                .compose(RxSchedulers.<FollowLive>applySchedulers())
                .subscribe(new Action1<FollowLive>() {
                               @Override
                               public void call(FollowLive followLive) {
                                   Log.i("======", "获取数据成功");
                                   Snackbar.make(fab, "获取数据成功", Snackbar.LENGTH_LONG)
                                           .setAction("Action", null).show();
                                   swipeRefreshLayout.setRefreshing(false);
                                   if (mAdapter != null) {
                                       mAdapter.notifyDataSetChanged();
                                   } else {
                                       mAdapter = new FollowUserAdapter(R.layout.item_follow_user, followLive.getData());
                                       recyclerviewFollow.setAdapter(mAdapter);
                                   }
                               }
                           }
                        , new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                swipeRefreshLayout.setRefreshing(false);
                                Log.i("======", throwable.toString());
                            }
                        }
                );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_clear) {
            Snackbar.make(fab, "Clear Token", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            SharedPreferencesUtils.saveToken(MainActivity.this, null);
            return true;
        } else if (id == R.id.action_refresh) {
            Snackbar.make(fab, "重新请求数据", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            getFollowLive();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 参考自这篇文章http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2016/0206/3953.html,retrywhen(Func1 notificationHandler)中notificationHandler只会被执行一遍
     * 但是如何来实现不断重试呢，一个简单的小技巧，使用flatmap操作符。
     */
    public class RetryWithDelay implements
            Func1<Observable<? extends Throwable>, Observable<?>> {

        private final int maxRetries;
        private final int retryDelayMillis;
        private int retryCount;

        public RetryWithDelay(int maxRetries, int retryDelayMillis) {
            Log.i("======", "RetryWithDelay");
            this.maxRetries = maxRetries;
            this.retryDelayMillis = retryDelayMillis;
            this.retryCount = 0;
        }

        @Override
        public Observable<?> call(Observable<? extends Throwable> attempts) {
            return attempts.flatMap(new Func1<Throwable, Observable<?>>() {
                @Override
                public Observable<?> call(final Throwable throwable) {
                    if (++retryCount <= maxRetries) {
                        if (throwable instanceof Exception) {
                            //2016/12/20 有待改进 比如说登录接口返回较慢（慢的超过了retryCount*retryDelayMillis），那么会一直重试登录接口和follow数据接口，直到超过重试次数
                            // 测试方法：比如说将延迟时间1000改成1
                            //2017.1.12 已修改
                            Log.i("======", "重新登录");
                            //重新登录
                            //参考：https://mcxiaoke.gitbooks.io/rxdocs/content/operators/Blocking-Observable-Operators.html
                            // BlockingObservable的方法不是将一个Observable变换为另一个，也不是过滤Observables，它们会打断Observable的调用链，会阻塞等待直到Observable发射了想要的数据，然后返回这个数据
                            BlockingObservable.from(ApiClient.getInstance().getLiveApi().login(SharedPreferencesUtils.getUserName(mContext), SharedPreferencesUtils.getPassword(mContext))
                                    .compose(RxSchedulers.<UserInfo>applySchedulers())).subscribe(new Action1<UserInfo>() {
                                @Override
                                public void call(UserInfo userInfo) {
                                    Log.i("======", "登录成功");
                                    SharedPreferencesUtils.saveToken(mContext, userInfo.getData().getToken());
                                }
                            });
                            return Observable.timer(retryDelayMillis,
                                    TimeUnit.MILLISECONDS);
                        }
                    }
                    return Observable.error(throwable);
                }
            });
        }
    }
}
