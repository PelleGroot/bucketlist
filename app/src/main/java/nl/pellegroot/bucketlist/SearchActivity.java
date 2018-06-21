package nl.pellegroot.bucketlist;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.util.CrashUtils;

import java.net.Inet4Address;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements searchRequest.Callback {
    public double lat;
    public double lng;
    public String category;
    public LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Button btnBucketlist = findViewById(R.id.btn_bucketlist);
        Button btnProfile = findViewById(R.id.btn_profile);

        btnBucketlist.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SearchActivity.this, bucketlistActivity.class));
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SearchActivity.this, ProfileActivity.class));
            }
        });

        String[] categories = new String[]{
                "accommodation", "attraction", "restaurant", "poi"
        };
        final Spinner spinCategories = findViewById(R.id.category_spinner);
        ArrayAdapter<String> CategorySpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        spinCategories.setAdapter(CategorySpinnerAdapter);

        if (ContextCompat.checkSelfPermission(SearchActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(SearchActivity.this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        } if (ContextCompat.checkSelfPermission(SearchActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(SearchActivity.this, new String[] {android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }

        Button searchActivities = (Button) findViewById(R.id.btn_search_activity);
        searchActivities.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                category = (String) spinCategories.getSelectedItem();

                if (ContextCompat.checkSelfPermission(SearchActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(SearchActivity.this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION},
                            1);
                } if (ContextCompat.checkSelfPermission(SearchActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(SearchActivity.this, new String[] {android.Manifest.permission.ACCESS_COARSE_LOCATION},
                            1);
                }

                // TODO: add loading screen activity or smth
                mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);

            }
        });
    }

    @Override
    public void gotActivities(ArrayList<bucketListItem> activities){
        Intent intent = new Intent(SearchActivity.this, SearchResultActivity.class);
        intent.putExtra("ACTIVITIES", activities);
        startActivity(intent);
    }

    @Override
    public void gotActivitiesError(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            mLocationManager.removeUpdates(this);

            searchRequest searchReq = new searchRequest(SearchActivity.this);
            searchReq.getActivity(SearchActivity.this, lat, lng, category);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderDisabled(String s) {
            Toast.makeText(SearchActivity.this, "GPS is disabled", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String s) {

        }
    };
}
