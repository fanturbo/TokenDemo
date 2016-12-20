package war3.pub.tokendemo.api;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Call;

/**
 * Created by turbo on 2016/12/20.
 */

public class DemoInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
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


        if (isTokenExpired(bodyString)) {//根据和服务端的约定判断token过期
            throw new RuntimeException();
            //本身的解决办法是如果token过期，同步重新请求token，然后重新组装新的请求（即继续执行原先的请求数据操作）
        }
        return response;
    }

    private String getNewToken() throws IOException {
        return null;
    }

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
}