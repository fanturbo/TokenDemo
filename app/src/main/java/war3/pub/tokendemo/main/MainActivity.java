package war3.pub.tokendemo.main;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        recyclerviewFollow.setLayoutManager(new LinearLayoutManager(this));
        ApiClient.getInstance().getLiveApi().getFollow(SharedPreferencesUtils.getToken(this))
                .flatMap(new Func1<FollowLive, Observable<FollowLive>>() {
                    @Override
                    public Observable<FollowLive> call(FollowLive followLive) {
                        if (followLive.getError() == 903) {
                            return Observable.error(new Exception("kkkk"));
                        }
                        return Observable.just(followLive);
                    }
                })
                .retryWhen(new Func1<Observable<? extends Throwable>, Observable<Long>>() {
                    @Override
                    public Observable<Long> call(Observable<? extends Throwable> observable) {
                        return observable.zipWith(Observable.range(1, 4), new Func2<Throwable, Integer, Integer>() {
                            @Override
                            public Integer call(Throwable throwable, Integer i) {
                                return i;
                            }
                        }).flatMap(new Func1<Integer, Observable<? extends Long>>() {
                            @Override
                            public Observable<? extends Long> call(Integer retryCount) {
                                return Observable.timer(1, TimeUnit.SECONDS);
                            }
                        });
                    }
                })
                .compose(RxSchedulers.<FollowLive>applySchedulers())
                .subscribe(new Action1<FollowLive>() {
                    @Override
                    public void call(FollowLive followLive) {
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.i("======", throwable.toString());
                    }
                });
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
