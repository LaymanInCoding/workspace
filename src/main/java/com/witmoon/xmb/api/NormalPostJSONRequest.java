package com.witmoon.xmb.api;

import android.net.http.LoggingEventHandler;
import android.util.Log;

import com.duowan.mobile.netroid.AuthFailureError;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetworkResponse;
import com.duowan.mobile.netroid.ParseError;
import com.duowan.mobile.netroid.Request;
import com.duowan.mobile.netroid.Response;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义HTTP请求类
 * 使用Volley或Netroid自带的JsonObjectRequest请求对象提交post请求,如果有参数需要提交,
 * 就必须以JSONObject的json串方式提交; 服务端可能并不支持这种方式, 比如常见的spring mvc,
 * 就很难支持json的请求方式.
 * 因此自定义Request, 实现客户端以普通的post方式进行提交,服务端返回json串
 * <p>
 * Created by zhyh on 2015/5/6.
 */
public class NormalPostJSONRequest extends Request<JSONObject> {

    private Map<String, String> mParamMap;

    public static final String SERVER_PARAMS_KEY = "json";

    // 无HTTP参数构造方法
    public NormalPostJSONRequest(String url, Listener<JSONObject> listener) {
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
    public NormalPostJSONRequest(String url, JSONObject p, Listener<JSONObject> listener) {
        super(Method.POST, url, listener);
        mParamMap = new HashMap<>();
        mParamMap.put(SERVER_PARAMS_KEY, p.toString());
        Log.e("params", mParamMap.toString());
        Log.e("url", url);
    }

    /**
     * @see NormalPostJSONRequest
     */
    public NormalPostJSONRequest(String url, Map<String, String> pm,
                                 Listener<JSONObject> listener) {
        this(url, ApiHelper.getParamMap(pm), listener,"withoutJson");
    }
    /**
     *
     * 构造方法
     *
     * @param url      POST请求提交URL  不拼接Json
     * @param pm        参数, 最终会组成:{'key':'value','key2':'value2'}形式, 以符合服务器端参数要求
     * @param listener 响应监听
     */
    public NormalPostJSONRequest(String url, Map<String, String> pm,
                                 Listener<JSONObject> listener, String withoutJson) {
        super(Method.POST, url, listener);
        mParamMap = pm;
        Log.e("params",mParamMap.toString());
        Log.e("url", url);
    }


    public static Map<String, String> addMap(Map<String, String> map) {
        map.put("version", AppContext.geVerSion());
        map.put("channel", AppContext.getChannels());
        map.put("device", "android");
        return map;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, response.charset);
            return Response.success(new JSONObject(jsonString), response);
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