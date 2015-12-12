package com.brightcreations.ibeaconposition.util;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by mahmoud on 4/25/15.
 */
public final class VolleyHelper {
    private static final String TAG = VolleyHelper.class.getSimpleName();
    private static VolleyHelper mInstance;
    private static Context mContext;
    private RequestQueue mRequestQueue;

    private VolleyHelper(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized VolleyHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleyHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley
                    .newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);

    }

    public void cancelRequests(String tag) {
        mRequestQueue.cancelAll(tag);
    }


    public Request createPostRequest(String url, final JSONObject params,
                                     Response.Listener<JSONObject> successListener,
                                     Response.ErrorListener errorListener) {

        return new JsonObjectRequest(url,
                params,
                successListener,
                errorListener) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }

            @Override
            public byte[] getBody() {
                if (params != null) {
                    StringBuilder body = new StringBuilder();
                    Iterator<String> keys = params.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        try {
                            String value = params.get(key).toString();
                            body.append(key).append("=").append(value);
                            if (keys.hasNext()) {
                                body.append("&");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    return body.toString().getBytes();
                }
                return super.getBody();
            }
        };
    }
}
