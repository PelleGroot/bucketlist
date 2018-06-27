package nl.pellegroot.bucketlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/*
    in this activity the items location is searched for and found by using the google maps API
 */

public class ItemLocationMapsActivity extends FragmentActivity implements OnMapReadyCallback, OnConnectionFailedListener{
    public GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    public Place myPlace;
    public LatLng placeLatLng;
    public String placeName;
    public Boolean setMarker = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_location_maps);

        // obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // construct the API client from google
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        Intent intent = getIntent();

        // get the location ID from the intent
        if(intent.getStringExtra("LOCATION_ID") != null) {
            String placeId = (String) intent.getStringExtra("LOCATION_ID");

            // set the map to the location of the locationID
            mGeoDataClient.getPlaceById(placeId).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                    if (task.isSuccessful()) {
                        PlaceBufferResponse places = task.getResult();
                        myPlace = places.get(0);
                        placeLatLng = (LatLng) myPlace.getLatLng();
                        placeName = (String) myPlace.getName();
                        places.release();

                        mMap.addMarker(new MarkerOptions().position(placeLatLng).title(placeName));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(placeLatLng));
                    } else {
                        Log.e("Maps", "Place not found.");
                    }
                }
            });
        }

        // if the intent has a lat and lng, we use that to detemine the location of the item
        else if(intent.getStringExtra("LOCATION_LAT") != null){
            double lat = Double.parseDouble(intent.getStringExtra("LOCATION_LAT"));
            double lng = Double.parseDouble(intent.getStringExtra("LOCATION_LNG"));
            placeName = intent.getStringExtra("LOCATION_NAME");
            placeLatLng = new LatLng(lat, lng);

            // having this parameter is to make sure that the marker is set that the camera moves to the location only when the map is ready
            setMarker = true;
        }
        else{
            Toast.makeText(this, "No location found", Toast.LENGTH_SHORT).show();
        }
    }

    /*
        this manipulates the map to the position or zoom the it needed to be
        it only manipulates the map when it is available
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.zoomTo(17));

        // if the marker is set, move the map to the right place
        if(setMarker){
            mMap.addMarker(new MarkerOptions().position(placeLatLng).title(placeName));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(placeLatLng));
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection failed", Toast.LENGTH_SHORT).show();
    }
}
