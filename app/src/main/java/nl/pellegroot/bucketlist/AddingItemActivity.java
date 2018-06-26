package nl.pellegroot.bucketlist;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;

/*
    this activity allows you to add an item to the bucketlist
    this activity is also called when an existing item is being edited
 */

public class AddingItemActivity extends FragmentActivity implements OnConnectionFailedListener {

    protected PlaceDetectionClient mPlaceDetectionClient;
    protected GeoDataClient mGeoDataClient;
    private FirebaseAuth mAuth;
    private FirebaseUser curUser;
    private String curUserId;
    private GoogleApiClient mGoogleApiClient;
    private String placeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_item);

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

        // Initiate Firebase database connection
        mAuth = FirebaseAuth.getInstance();
        curUser = mAuth.getCurrentUser();
        curUserId = curUser.getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference userRef = database.getReference("Users").child(curUserId).child("bucketlist");

        // get the autocomplete location field
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                placeId = place.getId();
                Log.i("OnPlaceSelected", "Place: " + place.getId());
            }

            @Override
            public void onError(Status status) {
                Toast.makeText(AddingItemActivity.this, "Location search went wrong, try again", Toast.LENGTH_SHORT).show();
            }
        });

        // get the intent
        Intent intent = getIntent();

        // set the buttons and fields
        final EditText inputName = findViewById(R.id.input_name);
        final EditText inputDescription = findViewById(R.id.input_description);
        Button btnAddToList = findViewById(R.id.btn_add_item_to_list);
        final bucketListItem newItem = new bucketListItem();

        // get the clicked item from the intent if there is one
        if(intent.getSerializableExtra("CLICKED_ITEM")!= null){

            // set safe button to a different text
            btnAddToList.setText("Save edited item");

            // set the fields to show entries from the retrieved item
            bucketListItem clickedItem = (bucketListItem) intent.getSerializableExtra("CLICKED_ITEM");
            final String clickedItemId = intent.getStringExtra("ITEMID");
            inputName.setText(clickedItem.getName());
            inputDescription.setText(clickedItem.getDescription());

            // set the items in the database to the edited fields
            btnAddToList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    newItem.setName(inputName.getText().toString());
                    newItem.setDescription(inputDescription.getText().toString());

                    userRef.child(clickedItemId).child("name").setValue(newItem.getName());
                    userRef.child(clickedItemId).child("description").setValue(newItem.getDescription());

                    // if the location is nog changed, it will not change the location in the database to null
                    if(placeId != null){
                        newItem.setLocation(placeId);
                        userRef.child(clickedItemId).child("location").setValue(newItem.getLocation());
                    }

                    // start new activity with the new clicked item
                    Intent intent = new Intent(AddingItemActivity.this, bucketListItemActivity.class);
                    intent.putExtra("CLICKED_ITEM",(Serializable) newItem);
                    startActivity(intent);
                }
            });
        }
        else {

            // if there is no item in the intent, it will start a empty activity to add a new item to the DB
            btnAddToList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    newItem.setName(inputName.getText().toString());
                    newItem.setDescription(inputDescription.getText().toString());
                    newItem.setLocation(placeId);

                    userRef.push().setValue(newItem);
                    startActivity(new Intent(AddingItemActivity.this, bucketlistActivity.class));
                }
            });
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection failed", Toast.LENGTH_SHORT).show();
    }
}