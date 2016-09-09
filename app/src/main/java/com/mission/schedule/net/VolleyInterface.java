package com.mission.schedule.net;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * Created by lenovo on 2015/12/5.
 */
public abstract class VolleyInterface {

    public Context context;
    public static Response.Listener<String> listener;
    public static Response.ErrorListener errorListener;

    public abstract void onMySuccess(String result);
    public abstract void onMyError(VolleyError volleyError);

    public VolleyInterface(Context context, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        this.context = context;
        this.listener = listener;
        this.errorListener = errorListener;
    }

    public Response.Listener<String> loadingListener() {
        listener = new Response.Listener<String>(){

            @Override
            public void onResponse(String s) {
                onMySuccess(s);
            }
        };
        return listener;
    }
    public Response.ErrorListener loadingErrorListener(){
        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                onMyError(volleyError);
            }
        };
        return  errorListener;
    }
}

