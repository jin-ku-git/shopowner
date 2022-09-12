package com.youwu.shopowner.data.source;

import com.youwu.shopowner.bean.UpDateBean;
import com.youwu.shopowner.entity.DemoEntity;

import io.reactivex.Observable;
import me.goldze.mvvmhabit.http.BaseBean;
import me.goldze.mvvmhabit.http.BaseResponse;

/**
 * Created by goldze on 2019/3/26.
 */
public interface HttpDataSource {
    //模拟登录
    Observable<Object> login();

    //模拟上拉加载
    Observable<DemoEntity> loadMore();

    Observable<BaseResponse<DemoEntity>> demoGet();

    Observable<BaseResponse<DemoEntity>> demoPost(String catalog);


    //检查更新
    Observable<BaseBean<UpDateBean>> GET_APP_VERSION();

    //登录
    Observable<BaseBean<Object>> LOGIN(String name, String password);

    //获取个人信息
    Observable<BaseBean<Object>> GET_ME();
}
