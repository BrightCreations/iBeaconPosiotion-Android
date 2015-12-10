package com.brightcreations.ibeaconposition.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.brightcreations.ibeaconposition.util.VolleyHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mahmoudhabib on 12/10/15.
 */
public class SubmitPositionService extends IntentService {
    public static final String ACTION_SUBMIT_POSITION_SUCCESS = "com.brightcreations.ibeaconposition.action.SUBMIT_POSITION_SUCCESS";
    public static final String ACTION_SUBMIT_POSITION_ERROR = "com.brightcreations.ibeaconposition.action.SUBMIT_POSITION_ERROR";

    public static final String EXTRA_NAME = "com.brightcreations.ibeaconposition.extra.NAME";
    public static final String EXTRA_BEACON_ID = "com.brightcreations.ibeaconposition..extra.BEACON_ID";

    private static final String TAG = SubmitPositionService.class.getSimpleName();

    private static final String SUBMIT_POSITION_URL = "http://riseup.brightcreations.com/api/users/add ";

    private static final String PARAM_NAME = "name";
    private static final String PARAM_BEACON = "beacon";

    private static final String ARG_STATUS = "status";
    private static final String ARG_RESULT = "result";
    private static final String ARG_MESSAGE = "message";

    private static final int STATUS_OK = 200;

    public SubmitPositionService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String name = intent.getStringExtra(EXTRA_NAME);
        String beaconId = intent.getStringExtra(EXTRA_BEACON_ID);

        if (name != null && !name.isEmpty() && beaconId != null && !beaconId.isEmpty()) {
            submitPosition(name, beaconId);
        }
    }

    private void submitPosition(String name, String beaconId) {
        VolleyHelper volleyHelper = VolleyHelper.getInstance(getApplicationContext());
        Map<String, String> params = new HashMap<>();
        params.put(PARAM_NAME, name);
        params.put(PARAM_BEACON, beaconId);
        Request request = volleyHelper
                .createPostRequest(SUBMIT_POSITION_URL, new JSONObject(params),
                        new OnPositionSubmitSuccessListener(),
                        new OnPositionSubmitErrorListener());
        volleyHelper.addToRequestQueue(request);
    }

    private void submitPositionSuccess() {
        sendBroadcast(ACTION_SUBMIT_POSITION_SUCCESS);
    }

    private void submitPositionError() {
        sendBroadcast(ACTION_SUBMIT_POSITION_ERROR);
    }

    private void sendBroadcast(String action) {
        Intent intent = new Intent(action);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    private class OnPositionSubmitSuccessListener implements Response.Listener<JSONObject> {

        @Override
        public void onResponse(JSONObject response) {
            try {
                int status = response.getInt(ARG_STATUS);
                Log.i(TAG, response.toString());
                if (status == STATUS_OK) {
                    submitPositionSuccess();
                } else {
                    submitPositionError();
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error Reading Server Response", e);
                submitPositionError();
            }
        }
    }

    private class OnPositionSubmitErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            submitPositionError();
        }
    }
}
