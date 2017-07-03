package war3.pub.tokendemo.api;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;
import war3.pub.tokendemo.model.CodingUser;
import war3.pub.tokendemo.model.FollowLive;
import war3.pub.tokendemo.model.UserInfo;

/**
 * Created by snail on 16/10/30.
 */
public interface ILiveApi {

    @GET("/api/v1/login")
    Observable<UserInfo> login(@Query("username") String username, @Query("password") String password);

    @GET("/api/v1/remind_list")
    Observable<FollowLive> getFollow(@Query("token") String token);

    @FormUrlEncoded
    @POST("/api/login")
    Observable<Response<BaseResult>> loginCoding(@Field("email") String email, @Field("password") String password);

    /**
     * 同步登录接口
     *
     */
    @FormUrlEncoded
    @POST("/api/login")
    Call<BaseResult> loginCodingSynch(@Field("email") String email, @Field("password") String password);

    @GET("/api/account/current_user")
    Observable<BaseResult<CodingUser>> getCurrentUser();
}
