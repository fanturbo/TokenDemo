package war3.pub.tokendemo.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import war3.pub.tokendemo.global.Constants;

/**
 * Created by snail on 16/10/30.
 */
public class ApiClient {
    private static final int DEFAULT_TIMEOUT = 10000;

    private static Retrofit retrofit;

    private static ILiveApi mLiveApi;

    private static ApiClient apiClient;

    public static ApiClient getInstance() {
        if (apiClient == null) {
            apiClient = new ApiClient();
        }
        return apiClient;
    }

    public ILiveApi getLiveApi() {
        return mLiveApi == null ? configRetrofit(ILiveApi.class) : mLiveApi;
    }


    private <T> T configRetrofit(Class<T> service) {
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
        okHttpClient.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS).addInterceptor(new DemoInterceptor());
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.baseUrl)
                .client(okHttpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        return retrofit.create(service);

    }
}
