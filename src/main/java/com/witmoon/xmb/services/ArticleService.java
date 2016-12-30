package com.witmoon.xmb.services;

import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.model.MajorArticle;
import com.witmoon.xmb.model.mbq.MbqArticleCollectResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import rx.Observable;

public class ArticleService {
    private static final String USER_SERVER_URL = "https://api.xiaomabao.com";
    private Api api;

    public ArticleService() {
        RequestInterceptor requestInterceptor = (RequestInterceptor.RequestFacade request) -> request.addHeader("Accept", "application/json");

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(USER_SERVER_URL)
                .setRequestInterceptor(requestInterceptor)
                .build();

        api = restAdapter.create(Api.class);
    }

    public Api getApi() {
        return api;
    }

    public interface Api {
        @FormUrlEncoded
        @POST("/article/article_list")
        Observable<List<MajorArticle>> article_list(@FieldMap Map<String, String> params);

        @FormUrlEncoded
        @POST("/article/collect_list")
        Observable<List<MajorArticle>> collect_list(@FieldMap Map<String, String> params);

        @FormUrlEncoded
        @POST("/article/collect")
        Observable<MbqArticleCollectResponse> collect(@FieldMap Map<String, String> params);

        @FormUrlEncoded
        @POST("/article/check_collect")
        Observable<MbqArticleCollectResponse> check_collect(@FieldMap Map<String, String> params);
    }

    public static HashMap<String,String> gen_article_list_params(int page){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("page",page+"");
        return hashMap;
    }

    public static HashMap<String,String> gen_article_search_list_params(String keyword,int page){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("keyword",keyword);
        hashMap.put("page",page+"");
        return hashMap;
    }

    public static HashMap<String,String> gen_collect_list_params(int page){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("session[sid]", AppContext.getLoginInfo().getSid());
        hashMap.put("session[uid]", AppContext.getLoginInfo().getUid() + "");
        hashMap.put("page", page + "");
        return hashMap;
    }

    public static HashMap<String,String> gen_collect_params(String article_id){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("session[sid]", AppContext.getLoginInfo().getSid());
        hashMap.put("session[uid]", AppContext.getLoginInfo().getUid() + "");
        hashMap.put("article_id",article_id);
        return hashMap;
    }

}
