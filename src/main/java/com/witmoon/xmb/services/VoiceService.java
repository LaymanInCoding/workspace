package com.witmoon.xmb.services;

import com.witmoon.xmb.model.Voice;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import rx.Observable;

public class VoiceService {
    private static final String USER_SERVER_URL = "http://api.xiaomabao.com/";
    private VoiceApi api;

    public VoiceService() {
        RequestInterceptor requestInterceptor = (RequestInterceptor.RequestFacade request) -> request.addHeader("Accept", "application/json");

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(USER_SERVER_URL)
                .setRequestInterceptor(requestInterceptor)
                .build();

        api = restAdapter.create(VoiceApi.class);
    }

    public VoiceApi getApi() {
        return api;
    }

    public interface VoiceApi {
        @FormUrlEncoded
        @POST("/voice/voice_list")
        Observable<List<Voice>> voice_list(@FieldMap Map<String, String> params);

        @FormUrlEncoded
        @POST("/voice/voice_click")
        Observable<JSONObject> voice_click(@FieldMap Map<String, String> params);

        @FormUrlEncoded
        @POST("/voice/voice_detail")
        Observable<Voice> voice_detail(@FieldMap Map<String, String> params);
    }

    public static HashMap<String,String> gen_voice_list_params(int page){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("page",page+"");
        return hashMap;
    }

    public static HashMap<String,String> gen_voice_search_list_params(String keyword,int page){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("page",page+"");
        hashMap.put("keyword",keyword);
        return hashMap;
    }

    public static HashMap<String,String> gen_voice_click_params(String id){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("id",id+"");
        return hashMap;
    }

    public static HashMap<String,String> gen_voice_detail_params(String id){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("id",id+"");
        return hashMap;
    }

}
