package nl.pellegroot.bucketlist;


import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.util.CrashUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class searchRequest implements Response.Listener<JSONObject>, Response.ErrorListener {
    public Context context;
    public Callback callback;

    public searchRequest(Context searchContext) {
        context = searchContext;
    }

    public interface Callback {
        void gotActivities(ArrayList<bucketListItem> activities);
        void gotActivitiesError(String message);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        callback.gotActivitiesError(error.getMessage());
    }

    @Override
    public void onResponse(JSONObject response) {
        JSONArray jsonArray;
        ArrayList<bucketListItem> activities = new ArrayList<>();
        Log.d("stuff", "onResponse: inside the onresponse of the request");
        Log.d("resultOfReq", "onResponse: " + response);

        try{
            jsonArray = response.getJSONArray("");

            for(int i=0; i<jsonArray.length(); i++){
                bucketListItem newBucketlistItem = new bucketListItem();
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                newBucketlistItem.setName(jsonObject.getString("name"));
                newBucketlistItem.setDescription(jsonObject.getString("category"));
                newBucketlistItem.setLat(jsonObject.getString("lat"));
                newBucketlistItem.setLng(jsonObject.getString("lng"));

                Log.d("stuff", "onResponse of searchrequest: ");
                activities.add(newBucketlistItem);
            }
        } catch(JSONException e){
                e.printStackTrace();
        }

        callback.gotActivities(activities);
    }

    public void getActivity(Callback callbackActivity, double lat, double lng, String category) {
        callback = callbackActivity;

        Log.d("stuff", "getActivity inside creating the request: ");

        RequestQueue queue = Volley.newRequestQueue(context);

//        String url = String.format("http://tour-pedia.org/api/getPlaces?category=%s&location=%s", category, location);
        String url = String.format("http://tour-pedia.org/api/getPlacesByArea?S=%2.3f&N=%2.3f&W=4&E=5&category=%s",lat, lng, category);

        Log.d("stuff", "getActivity: " + url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, this, this);
        queue.add(jsonObjectRequest);

    }
}

