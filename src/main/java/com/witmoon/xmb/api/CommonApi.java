package com.witmoon.xmb.api;

import com.duowan.mobile.netroid.Listener;
import com.witmoon.xmb.api.alipay.NormalGetJSONRequest;

import org.json.JSONObject;

/**
 * Created by de on 2016/11/21.
 */
public class CommonApi {

    public static void getSearchWord(Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalGetJSONRequest(ApiHelper.BASE_URL + ("/search/recommond"), listener));
    }
}
