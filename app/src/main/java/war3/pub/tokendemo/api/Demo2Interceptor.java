package war3.pub.tokendemo.api;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import rx.Subscriber;
import war3.pub.tokendemo.App;
import war3.pub.tokendemo.global.Constants;
import war3.pub.tokendemo.global.TokenInvalideException;
import war3.pub.tokendemo.util.SharedPreferencesUtils;
import war3.pub.tokendemo.util.SimpleSHA1;

/**
 * Created by turbo on 2016/12/20.
 */

public class Demo2Interceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder requestBuilder = chain.request().newBuilder();
        String cookie = SharedPreferencesUtils.getCookie(App.getContext());
        if (cookie != null)
            requestBuilder.addHeader("Cookie", cookie);
        okhttp3.Response response = chain.proceed(requestBuilder.build());

        ResponseBody responseBody = response.body();
        BufferedSource source = responseBody.source();
        source.request(Long.MAX_VALUE); // Buffer the entire body.
        Buffer buffer = source.buffer();
        Charset charset = Charset.forName("UTF8");
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            charset = contentType.charset(charset);
        }
        String bodyString = buffer.clone().readString(charset);


        if (isTokenExpired2(bodyString)) {//根据和服务端的约定判断token过期
            //本身的解决办法是如果token过期，同步重新请求token，然后重新组装新的请求（即继续执行原先的请求数据操作）
            String newToken = getNewToken();
            if (newToken != null) {
                //使用新的Token，创建新的请求
                return getResponse2(chain, request, response, newToken);
            } else {
                throw new TokenInvalideException();
            }
        }
        return response;
    }

    /**
     * 在请求路径中添加token的方法
     *
     * @param chain
     * @param request
     * @param response
     * @param newToken
     * @return
     * @throws IOException
     */
    private Response getResponse1(Chain chain, Request request, Response response, String newToken) throws IOException {
        HttpUrl.Builder http = new HttpUrl.Builder()
                .scheme("http")
                .addPathSegment(request.url().encodedPathSegments().get(0))
                .host(Constants.baseUrl);
        Set<String> queryParameterNames = request.url().queryParameterNames();
        for (int i = 0; i < queryParameterNames.size(); i++) {
            String queryParameterName = request.url().queryParameterName(i);
            if (!"token".equals(queryParameterName)) {
                http.addQueryParameter(queryParameterName, request.url().queryParameterValue(i));
            } else {
                http.addQueryParameter("token", newToken);
            }
        }
        Request newRequest = request.newBuilder().url(http.build())
                .build();
        response.body().close();
        //重新请求
        return chain.proceed(newRequest);
    }

    /**
     * 在header中添加cookie的方法
     *
     * @param chain
     * @param request
     * @param response
     * @param newToken
     * @return
     * @throws IOException
     */
    private Response getResponse2(Chain chain, Request request, Response response, String newToken) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        String cookie = SharedPreferencesUtils.getCookie(App.getContext());
        builder.addHeader("Cookie", cookie);
        response.body().close();
        return chain.proceed(builder.build());
    }

    /**
     * 获取新的token或者cookie
     * @return
     * @throws IOException
     */
    private String getNewToken() throws IOException {
        retrofit2.Response<BaseResult> response = ApiClient.getInstance().getLiveApi()
                .loginCodingSynch(SharedPreferencesUtils.getUserName(App.getContext()), SharedPreferencesUtils.getPassword(App.getContext()))
                .execute();
        try {
            if (response.body().getCode() == 0) {
                //保存Cookie
                List<String> cookies = response.headers().values("Set-Cookie");//获取所有cookies
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < cookies.size(); i++) {
                    if (!(i == cookies.size())) {
                        sb.append(cookies.get(i) + ";");
                    } else {
                        sb.append(cookies.get(i));
                    }
                }
                SharedPreferencesUtils.saveCookie(App.getContext(), sb.toString());
                return sb.toString();
            } else {
                throw new TokenInvalideException();
            }
        } catch (Exception e) {
            throw new TokenInvalideException();
        }
    }

    /**
     * 判断斗鱼token是否过期
     *
     * @param bodyString
     * @return
     */
    private boolean isTokenExpired(String bodyString) {
        try {
            JSONObject jsonObject = new JSONObject(bodyString);
            if (jsonObject.getInt("error") == 903 || jsonObject.getInt("error") == 901) {
                return true;
            }
        } catch (Exception e) {
            return true;
        }
        return false;
    }

    /**
     * 判断coding是否登录 后者cookie过期
     *
     * @param bodyString
     * @return
     */
    private boolean isTokenExpired2(String bodyString) {
        try {
            JSONObject jsonObject = new JSONObject(bodyString);
            if (jsonObject.getInt("code") == 1000) {
                return true;
            }
        } catch (Exception e) {
            return true;
        }
        return false;
    }
}