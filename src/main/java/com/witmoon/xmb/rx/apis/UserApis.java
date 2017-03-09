package com.witmoon.xmb.rx.apis;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by de on 2017/2/27.
 */
public interface UserApis {

    @FormUrlEncoded
    @POST("refund/refund_search")
    Observable<ResponseBody> getRefundSearch(@FieldMap Map<String,String> map);

}
