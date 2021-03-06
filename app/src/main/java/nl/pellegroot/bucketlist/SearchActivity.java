package nl.pellegroot.bucketlist;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/*
    in this activity the user can search for items in specific categories
    the result will be within range of the users location
 */

public class SearchActivity extends AppCompatActivity implements SearchRequest.Callback {
    public double lat;
    public double lng;
    public int perimeter;
    public String category;
    public LocationManager mLocationManager;
    public TextView showPerimeter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // set the categories
        String[] categories = new String[]{
                "accommodation", "attraction", "restaurant", "poi"
        };
        final Spinner spinCategories = findViewById(R.id.category_spinner);
        ArrayAdapter<String> CategorySpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, categories);
        spinCategories.setAdapter(CategorySpinnerAdapter);

        showPerimeter = (TextView) findViewById(R.id.show_perimeter);
        final SeekBar searchPerimeter = (SeekBar) findViewById(R.id.perimeter_bar);

        // makes sure the user can see the selected perimeter
        searchPerimeter.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                perimeter = i;
                showPerimeter.setText(String.valueOf(perimeter));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        checkPermission();

        Button searchActivities = (Button) findViewById(R.id.btn_search_activity);
        searchActivities.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                category = (String) spinCategories.getSelectedItem();
                perimeter = searchPerimeter.getProgress();
                checkPermission();

                mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        0, 0, mLocationListener);

            }
        });

        // set the menu
        Button btnBucketlist = findViewById(R.id.btn_bucketlist);
        Button btnProfile = findViewById(R.id.btn_profile);

        btnBucketlist.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SearchActivity.this, BucketlistActivity.class));
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SearchActivity.this, ProfileActivity.class));
            }
        });
    }

    @Override
    public void gotActivities(ArrayList<BucketListItem> activities){
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

            SearchRequest searchReq = new SearchRequest(SearchActivity.this);
            searchReq.getActivity(SearchActivity.this, lat, lng, category, perimeter);
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

    private void checkPermission(){

        // checks location permissions and if not authorized, it asks for permission
        if (ContextCompat.checkSelfPermission(SearchActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(SearchActivity.this,
                    new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        } if (ContextCompat.checkSelfPermission(SearchActivity.this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(SearchActivity.this,
                    new String[] {android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }
    }
}
