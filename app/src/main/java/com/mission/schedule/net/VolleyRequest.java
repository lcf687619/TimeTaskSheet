package com.mission.schedule.net;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.mission.schedule.applcation.App;

import java.util.Map;

/**
 * Created by lenovo on 2015/12/5.
 */
public class VolleyRequest {

    public static StringRequest stringRequest;
    public static Context context;

    public static void RequestGet(Context context,String url, String tag, VolleyInterface vif){
        App.getHttpQueues().cancelAll(tag);
        stringRequest = new StringRequest(Request.Method.GET,url,vif.loadingListener(),vif.loadingErrorListener());
        stringRequest.setTag(tag);
        App.getHttpQueues().add(stringRequest);
        App.getHttpQueues().start();
    }
    public static void RequestPost(Context context,String url,String tag, final Map<String,String> params,VolleyInterface vif){
        App.getHttpQueues().cancelAll(tag);
        stringRequest = new StringRequest(Request.Method.POST,url,vif.loadingListener(),vif.loadingErrorListener()){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        stringRequest.setTag(tag);
        App.getHttpQueues().add(stringRequest);
        App.getHttpQueues().start();
    }
}
