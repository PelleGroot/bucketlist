package nl.pellegroot.bucketlist;


import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

public class searchRequest implements Response.Listener<JSONArray>, Response.ErrorListener {
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
    public void onResponse(JSONArray response) {
        ArrayList<bucketListItem> activities = new ArrayList<>();
        Log.d("stuff", "onResponse: inside the onresponse of the request");
        Log.d("resultOfReq", "onResponse: " + response);

        try{
            for(int i=0; i<response.length(); i++){
                bucketListItem newBucketlistItem = new bucketListItem();
                JSONObject jsonObject = response.getJSONObject(i);

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

        String url = String.format(Locale.US, "http://tour-pedia.org/api/getPlacesByArea?S=%2.3f&N=%2.3f&W=%1.0f&E=%1.0f&category=%s",lat,(lat + 0.3), lng, (lng+1), category);

        Log.d("stuff", "getActivity: " + url);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, this, this);
        queue.add(jsonArrayRequest);
    }
}

