package com.youwu.shopowner.data.source.http.service;

import com.youwu.shopowner.bean.UpDateBean;
import com.youwu.shopowner.entity.DemoEntity;

import io.reactivex.Observable;
import me.goldze.mvvmhabit.http.BaseBean;
import me.goldze.mvvmhabit.http.BaseResponse;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by goldze on 2017/6/15.
 */

public interface DemoApiService {
    @GET("action/apiv2/banner?catalog=1")
    Observable<BaseResponse<DemoEntity>> demoGet();

    @FormUrlEncoded
    @POST("action/apiv2/banner")
    Observable<BaseResponse<DemoEntity>> demoPost(@Field("catalog") String catalog);

    /**
     * 检查更新
     * @return
     */
    @GET("app_version")
    Observable<BaseBean<UpDateBean>> GET_APP_VERSION();

    /**
     * 登录
     *
     * @param tel      账号
     * @param password 密码
     * @return
     */
    @FormUrlEncoded
    @POST("auth/login")
    Observable<BaseBean<Object>> LOGIN(@Field("tel") String tel, @Field("password") String password);

    /**
     * 获取个人信息
     *
     * @return
     */

    @POST("auth/me")
    Observable<BaseBean<Object>> GET_ME();
}
