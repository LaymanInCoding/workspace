package com.witmoon.xmb.api;

import android.util.Log;

import com.duowan.mobile.netroid.AuthFailureError;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetworkResponse;
import com.duowan.mobile.netroid.ParseError;
import com.duowan.mobile.netroid.Request;
import com.duowan.mobile.netroid.Response;
import com.witmoon.xmb.AppContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by de on 2017/1/4.
 */
public class NormalPostJsonArrayRequest extends Request<JSONArray> {

    private Map<String, String> mParamMap;

    public static final String SERVER_PARAMS_KEY = "json";

    // 无HTTP参数构造方法
    public NormalPostJsonArrayRequest(String url, Listener<JSONArray> listener) {
        super(Method.POST, url, listener);
        mParamMap = new HashMap<>();
    }

    /**
     * 构造方法
     *
     * @param url      POST请求提交URL
     * @param p        参数, 最终会组成"json":{'key':'value','key2':'value2'}形式, 以符合服务器端参数要求
     * @param listener 响应监听
     */
    public NormalPostJsonArrayRequest(String url, JSONObject p, Listener<JSONArray> listener) {
        super(Method.POST, url, listener);
        mParamMap = new HashMap<>();
        mParamMap.put(SERVER_PARAMS_KEY, p.toString());
        Log.e("params", mParamMap.toString());
        Log.e("url", url);
    }

    /**
     * @see NormalPostJSONRequest
     */
    public NormalPostJsonArrayRequest(String url, Map<String, String> pm,
                                      Listener<JSONArray> listener) {
        this(url, ApiHelper.getParamMap(pm), listener, "withoutJson");
    }

    /**
     * 构造方法
     *
     * @param url      POST请求提交URL  不拼接Json
     * @param pm       参数, 最终会组成:{'key':'value','key2':'value2'}形式, 以符合服务器端参数要求
     * @param listener 响应监听
     */
    public NormalPostJsonArrayRequest(String url, Map<String, String> pm,
                                      Listener<JSONArray> listener, String withoutJson) {
        super(Method.POST, url, listener);
        mParamMap = pm;
        Log.e("params", mParamMap.toString());
        Log.e("url", url);
    }


    public static Map<String, String> addMap(Map<String, String> map) {
        map.put("version", AppContext.geVerSion());
        map.put("channel", AppContext.getChannels());
        map.put("device", "android");
        return map;
    }

    @Override
    protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, response.charset);
            return Response.success(new JSONArray(jsonString), response);
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    public Map<String, String> getParams() throws AuthFailureError {
        return mParamMap;
    }

}
