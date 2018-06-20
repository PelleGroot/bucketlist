package nl.pellegroot.bucketlist;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.util.CrashUtils;
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
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        Intent intent = getIntent();
        if(intent.getStringExtra("LOCATION_ID") != null) {
            String placeId = (String) intent.getStringExtra("LOCATION_ID");

            mGeoDataClient.getPlaceById(placeId).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                    if (task.isSuccessful()) {
                        PlaceBufferResponse places = task.getResult();
                        myPlace = places.get(0);
                        Log.i("Maps", "Place found: " + myPlace.getName());
                        Log.d("stuff", "onComplete: " + myPlace.getLatLng());
                        placeLatLng = (LatLng) myPlace.getLatLng();
                        Log.d("stuff", "onComplete: " + placeLatLng);
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
        else if(intent.getStringExtra("LOCATION_LAT") != null){
            double lat = Double.parseDouble(intent.getStringExtra("LOCATION_LAT"));
            double lng = Double.parseDouble(intent.getStringExtra("LOCATION_LNG"));
            placeName = intent.getStringExtra("LOCATION_NAME");

            Log.d("stuff", "onCreate: " + lat + "," + lng);
            placeLatLng = new LatLng(lat, lng);
            setMarker = true;
        }
        else{
            Toast.makeText(this, "No location found", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        mMap.addMarker(new MarkerOptions().position(placeLatLng).title(placeName));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(placeLatLng));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(17));

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
