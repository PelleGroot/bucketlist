package nl.pellegroot.bucketlist;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;

/*
    this is the main screen of the app
    here the app will load all the bucketlist items in the users database
 */

public class BucketlistActivity extends AppCompatActivity {

private FirebaseAuth mAuth;
private FirebaseUser curUser;
private String curUserId;
private ArrayList<BucketListItem> bucketList = new ArrayList<>();

    @Override
    protected void onResume() {
        super.onResume();

        // doing this in the onResume makes sure that the list doesn't append when coming back from another activity
        // get a connection to the firebase DB
        mAuth = FirebaseAuth.getInstance();
        curUser = mAuth.getCurrentUser();
        curUserId = curUser.getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("Users").child(curUserId).child("bucketlist");

        // get the data from the database
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // empty the bucketlist so we don't have duplicates
                bucketList.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){

                    // get the bucketlist items from the database
                    BucketListItem bucketItem = new BucketListItem();
                    bucketItem.setName(postSnapshot.getValue(BucketListItem.class).getName());
                    bucketItem.setActivityDone(postSnapshot.getValue(BucketListItem.class).getActivityDone());
                    bucketItem.setLocation(postSnapshot.getValue(BucketListItem.class).getLocation());
                    bucketItem.setDescription(postSnapshot.getValue(BucketListItem.class).getDescription());
                    bucketItem.setRating(postSnapshot.getValue(BucketListItem.class).getRating());
                    bucketItem.setPhoto(postSnapshot.getValue(BucketListItem.class).getPhoto());
                    bucketItem.setLng(postSnapshot.getValue(BucketListItem.class).getLng());
                    bucketItem.setLat(postSnapshot.getValue(BucketListItem.class).getLat());
                    bucketList.add(bucketItem);
                }

                // set the adapter to display the bucketlist items
                ListView Listview = findViewById(R.id.lv_bucketlist);
                if(bucketList!= null) {
                    Listview.setAdapter(new BucketListAdapter(BucketlistActivity.this, R.layout.activity_bucket_list_item, bucketList));
                    Listview.setOnItemClickListener(new ListViewItemClicked());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(BucketlistActivity.this, "Database connection went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bucketlist);

        // set the menu
        Button btnProfile = findViewById(R.id.btn_profile);
        Button btnSearch = findViewById(R.id.btn_search);

        btnProfile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BucketlistActivity.this, ProfileActivity.class));
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BucketlistActivity.this, SearchActivity.class));
            }
        });

        FloatingActionButton btnAddItem = findViewById(R.id.btn_add_item);

        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BucketlistActivity.this, AddingItemActivity.class));
            }
        });

    }

    private class ListViewItemClicked implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            BucketListItem clickedItem = (BucketListItem) adapterView.getItemAtPosition(i);
            Intent intent = new Intent(BucketlistActivity.this, BucketListItemActivity.class);
            intent.putExtra("CLICKED_ITEM", (Serializable) clickedItem);
            startActivity(intent);
        }
    }
}
