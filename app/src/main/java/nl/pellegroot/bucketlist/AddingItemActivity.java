package nl.pellegroot.bucketlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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
    private DatabaseReference userRef;
    private String clickedItemId;
    private EditText inputName;
    private EditText inputDescription;
    private BucketListItem newItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_item);

        // construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        // initiate Firebase database connection
        mAuth = FirebaseAuth.getInstance();
        curUser = mAuth.getCurrentUser();
        curUserId = curUser.getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        userRef = database.getReference("Users").child(curUserId).child("bucketlist");

        // get the autocomplete location field
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                placeId = place.getId();
            }

            @Override
            public void onError(Status status) {
                Toast.makeText(AddingItemActivity.this, "Location search went wrong, try again", Toast.LENGTH_SHORT).show();
            }
        });

        // get the intent
        Intent intent = getIntent();

        // set the buttons and fields
        inputName = findViewById(R.id.input_name);
        inputDescription = findViewById(R.id.input_description);
        Button btnAddToList = findViewById(R.id.btn_add_item_to_list);
        newItem = new BucketListItem();

        // get the clicked item from the intent if there is one
        if(intent.getSerializableExtra("CLICKED_ITEM")!= null){

            // set safe button to a different text
            btnAddToList.setText("Save edited item");

            // set the fields to show entries from the retrieved item
            final BucketListItem clickedItem = (BucketListItem) intent.getSerializableExtra("CLICKED_ITEM");
            clickedItemId = intent.getStringExtra("ITEMID");
            inputName.setText(clickedItem.getName());
            inputDescription.setText(clickedItem.getDescription());
        }

        // add item to the DB
        btnAddToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkItemName(inputName.getText().toString());
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection failed", Toast.LENGTH_SHORT).show();
    }

    // check if the item has the same name as an item in the DB
    public void checkItemName(String name){
        Query query = userRef.orderByChild("name").equalTo(name);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot itemFromDb : dataSnapshot.getChildren()){

                    // if the name is already in the DB check if the ID is the same
                    if(itemFromDb.getRef().getKey().equals(clickedItemId)){
                        setEditItemToDb();
                    }

                    // if the ID is not the same, create a toast message
                    else if(!itemFromDb.getRef().getKey().equals(clickedItemId)){
                        Toast.makeText(AddingItemActivity.this, "Name is already taken, please use a different name", Toast.LENGTH_LONG).show();
                    }
                }

                // if the name is no found in the database, create it
                if((!dataSnapshot.exists())&& clickedItemId == null){
                    setNewItemToDb();
                } else if((!dataSnapshot.exists())){
                    setEditItemToDb();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    // set the edited fields to the item in the DB
    public void setEditItemToDb(){
        newItem.setName(inputName.getText().toString());
        newItem.setDescription(inputDescription.getText().toString());

        userRef.child(clickedItemId).child("name").setValue(newItem.getName());
        userRef.child(clickedItemId).child("description").setValue(newItem.getDescription());

        // if the location is nog changed, it will not change the location in the database to null
        if (placeId != null) {
            userRef.child(clickedItemId).child("location").setValue(placeId);
        }

        // start new activity with the new clicked item
        Intent intent = new Intent(AddingItemActivity.this, BucketlistActivity.class);
        startActivity(intent);
    }

    // create a new item in the DB
    public void setNewItemToDb(){
        newItem.setName(inputName.getText().toString());
        newItem.setDescription(inputDescription.getText().toString());
        newItem.setLocation(placeId);
        userRef.push().setValue(newItem);
        startActivity(new Intent(AddingItemActivity.this, BucketlistActivity.class));
    }
}