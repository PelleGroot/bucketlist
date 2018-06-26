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

import java.util.ArrayList;
import java.util.Locale;

/*
    In this class the request for data is made to the tour-pedia API
*/

public class SearchRequest implements Response.Listener<JSONArray>, Response.ErrorListener {
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

        try{
            for(int i=0; i<response.length(); i++){
                bucketListItem newBucketlistItem = new bucketListItem();
                JSONObject jsonObject = response.getJSONObject(i);

                newBucketlistItem.setName(jsonObject.getString("name"));
                newBucketlistItem.setDescription(jsonObject.getString("category"));
                newBucketlistItem.setLat(jsonObject.getString("lat"));
                newBucketlistItem.setLng(jsonObject.getString("lng"));

                activities.add(newBucketlistItem);
            }
        } catch(JSONException e){
                e.printStackTrace();
        }

        callback.gotActivities(activities);
    }

    // create the API request
    public void getActivity(Callback callbackActivity, double lat, double lng, String category) {
        callback = callbackActivity;

        RequestQueue queue = Volley.newRequestQueue(context);

        // add filtering to the API
        String url = String.format(Locale.US, "http://tour-pedia.org/api/getPlacesByArea?S=%2.3f&N=%2.3f&W=%1.2f&E=%1.2f&category=%s",lat,(lat + 0.01), lng, (lng+0.01), category);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, this, this);
        queue.add(jsonArrayRequest);
    }
}

