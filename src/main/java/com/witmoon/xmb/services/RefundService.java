package com.witmoon.xmb.services;

import com.witmoon.xmb.rx.RetrofitHelper;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by de on 2017/2/27.
 */
public class RefundService {

    private static final String Base_Url = "https://api.xiaomabao.com";

    private RefundApi mApi;

    public RefundService() {
        RetrofitHelper mHelper = new RetrofitHelper();
        mApi = mHelper.getApiService(Base_Url, RefundApi.class);
    }

    public RefundApi getApi() {
        return mApi;
    }

    public interface RefundApi {
        @FormUrlEncoded
        @POST("refund/refund_search")
        Observable<ResponseBody> getRefundSearch(@FieldMap Map<String, String> map);
    }
}
