package com.witmoon.xmb.services;

import com.witmoon.xmb.model.Voice;
import com.witmoon.xmb.rx.RetrofitHelper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

public class VoiceService {
    private static final String USER_SERVER_URL = "https://api.xiaomabao.com/";
    private VoiceApi api;

    public VoiceService() {
        RetrofitHelper helper = new RetrofitHelper();
        api = helper.getApiService(USER_SERVER_URL,VoiceApi.class);
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
