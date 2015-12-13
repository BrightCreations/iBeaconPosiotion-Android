package com.brightcreations.ibeaconposition.beacon;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;

import com.brightcreations.ibeaconposition.math.Point2;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.Region;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mahmoudhabib on 12/8/15.
 */
public final class BeaconsHelper {
    public static BeaconsHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new BeaconsHelper(context);
        }
        return sInstance;
    }

    private static BeaconsHelper sInstance;

    private final String TAG = getClass().getSimpleName();

    private Context mContext;
    private OnFileLoadListener mOnFileLoadListener;

    private int mMapNorthCorrection;
    private int mPixelToCmRatio;
    
    private Map<String, BeaconData> mBeaconsMap;

    private BeaconsHelper(Context context) {
        mContext = context;
        mBeaconsMap = new HashMap<>();
    }

    public void init(String fileName) {
        loadFromFile(fileName);
    }

    public BeaconData getBeaconData(Beacon beacon) {
        String id = beacon.getId3().toString();
        return mBeaconsMap.get(id);
    }

    public List<BeaconData> getBeaconsData(List<Beacon> beacons) {
        List<BeaconData> beaconsdata = new ArrayList<>();
        for (Beacon beacon : beacons) {
            beaconsdata.add(getBeaconData(beacon));
        }
        return beaconsdata;
    }

    public List<Beacon> getMatchingBeacons(Collection<Beacon> beacons, Region region) {
        List<Beacon> matchingBeacons = new ArrayList<>();
        for (Beacon beacon : beacons) {
            if (beacon.getId1().toString().equalsIgnoreCase(region.getUniqueId())) {
                matchingBeacons.add(beacon);
            }
        }
        return matchingBeacons;
    }

    public void setOnFileLoadListener(OnFileLoadListener listener) {
        mOnFileLoadListener = listener;
    }

    private void loadFromFile(String fileName) {
        new LoadFromFileTask().execute(fileName);
    }

    private void loadFinish(boolean success) {
        if (mOnFileLoadListener != null) {
            if (success) {
                mOnFileLoadListener.onReady();
            } else {
                mOnFileLoadListener.onFail();
            }
        }
    }

    public interface OnFileLoadListener {
        void onReady();

        void onFail();
    }

    private class LoadFromFileTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String fileName = params[0];
            AssetManager assetManager = mContext.getAssets();
            try {
                InputStream is = assetManager.open(fileName);
                JSONObject beaconsJson = parse(is);
                initBeaconsData(beaconsJson);
                return true;
            } catch (IOException e) {
                Log.e(TAG, "Error Opening File " + fileName, e);
            } catch (JSONException e) {
                Log.e(TAG, "Error Parsing File " + fileName, e);
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            loadFinish(success);
        }

        private JSONObject parse(InputStream inputStream) throws IOException, JSONException {
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return new JSONObject(builder.toString());
        }

        private void initBeaconsData(JSONObject beaconsJson) throws JSONException {
            mMapNorthCorrection = beaconsJson.getInt(Constants.ATTR_MAP_NORTH_CORRECTION);
            Log.d(TAG, "Map North Correction " + mMapNorthCorrection);

            mPixelToCmRatio = beaconsJson.getInt(Constants.ATTR_PIXEL_TO_CM_RATIO);
            Log.d(TAG, "Pixel To Cm Ratio " + mPixelToCmRatio);

            mBeaconsMap.clear();

            JSONArray beacons = beaconsJson.getJSONArray(Constants.ATTR_JSON_BEACONS);
            for (int i = 0; i < beacons.length(); i++) {
                JSONObject beacon = beacons.getJSONObject(i);
                String id = beacon.getString(Constants.ATTR_JSON_ID);
                String posString = beacon.getString(Constants.ATTR_JSON_POSITION);
                Point2 pos = getPoint2FromString(posString);

                String roomPosString = beacon.getString(Constants.ATTR_JSON_ROOM_POSITION);
                Point2 roomPos = getPoint2FromString(roomPosString);

                BeaconData beaconData = new BeaconData(id, pos, roomPos);
                Log.d(TAG, "Beacon " + beaconData.toString());
                mBeaconsMap.put(id, beaconData);
            }
            Log.d(TAG, "No of Beacons " + mBeaconsMap.size());
        }

        private Point2 getPoint2FromString(String string) {
            String[] cords = string.split(",");
            int x = Integer.parseInt(cords[0]);
            int y = Integer.parseInt(cords[1]);
            return new Point2(x, y);
        }
    }
}
