package nl.pellegroot.bucketlist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/*
    this activity shows the details of the item that was clicked in the search for activities
 */

public class ResultItemActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser curUser;
    private String curUserId;
    private BucketListItem resultItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_item);

        // initiate Firebase database connection
        mAuth = FirebaseAuth.getInstance();
        curUser = mAuth.getCurrentUser();
        curUserId = curUser.getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference userRef = database.getReference("Users").child(curUserId).child("bucketlist");

        // get Intent and retrieve the item which is clicked
        Intent intent = getIntent();
        resultItem = (BucketListItem) intent.getSerializableExtra("CLICKED_ITEM");

        TextView resultNameField = (TextView) findViewById(R.id.result_item_name);
        TextView resultDescriptionField = (TextView) findViewById(R.id.result_item_description);

        resultNameField.setText(resultItem.getName());
        resultDescriptionField.setText(resultItem.getDescription());
        ImageButton locationItem = (ImageButton) findViewById(R.id.result_location_icon);

        // set the location
        locationItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(resultItem.getLat() != null) {

                    // go to the location on google maps
                    Intent intent1 = new Intent(ResultItemActivity.this, ItemLocationMapsActivity.class);
                    intent1.putExtra("LOCATION_LAT", resultItem.getLat());
                    intent1.putExtra("LOCATION_LNG", resultItem.getLng());
                    intent1.putExtra("LOCATION_NAME", resultItem.getName());
                    startActivity(intent1);
                }
                else
                    Toast.makeText(ResultItemActivity.this, "No location found", Toast.LENGTH_SHORT).show();
            }
        });

        // set the 'add to list' button
        Button addToList = (Button) findViewById(R.id.btn_add_to_list);
        addToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRef.push().setValue(resultItem);
                startActivity(new Intent(ResultItemActivity.this, BucketlistActivity.class));
            }
        });

        Button share = (Button) findViewById(R.id.btn_share_result);
        share.setOnClickListener(new shareActivity());
    }

    private class shareActivity implements View.OnClickListener{

        @Override
        public void onClick(View view) {

            // on clicked, share the item through the medium the user chooses as text
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, resultItem.getName());
            sendIntent.putExtra(Intent.EXTRA_TEXT, resultItem.getDescription());
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Share activity"));
        }
    }
}
