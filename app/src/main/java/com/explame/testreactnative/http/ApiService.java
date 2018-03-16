package com.explame.testreactnative.http;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * RESTful 客户端接口服务
 * baseUrl : https://xiaoguoqi.com/
 * 具体调用及参数说明参考 com.first.saccelerator.NetworkTest
 * <p/>
 * Created by ZhengSheng on 2017/3/21.
 */

public interface ApiService {
    @GET
    Call<ResponseBody> downloadFileWithDynamicUrlSync(@Url String fileUrl);
}