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


public class bucketlistActivity extends AppCompatActivity {

private FirebaseAuth mAuth;
private FirebaseUser curUser;
private String curUserId;
private ArrayList<bucketListItem> bucketList = new ArrayList<>();

    @Override
    protected void onResume() {
        super.onResume();
        mAuth = FirebaseAuth.getInstance();
        curUser = mAuth.getCurrentUser();
        curUserId = curUser.getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("Users").child(curUserId).child("bucketlist");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bucketList.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    bucketListItem bucketItem = new bucketListItem();
                    bucketItem.setName(postSnapshot.getValue(bucketListItem.class).getName());
                    bucketItem.setActivityDone(postSnapshot.getValue(bucketListItem.class).getActivityDone());
                    bucketItem.setLocation(postSnapshot.getValue(bucketListItem.class).getLocation());
                    bucketItem.setDescription(postSnapshot.getValue(bucketListItem.class).getDescription());
                    bucketItem.setRating(postSnapshot.getValue(bucketListItem.class).getRating());
                    bucketItem.setPhoto(postSnapshot.getValue(bucketListItem.class).getPhoto());
                    bucketItem.setLng(postSnapshot.getValue(bucketListItem.class).getLng());
                    bucketItem.setLat(postSnapshot.getValue(bucketListItem.class).getLat());
                    bucketList.add(bucketItem);
                }

                ListView Listview = findViewById(R.id.lv_bucketlist);
                if(bucketList!= null) {
                    Listview.setAdapter(new bucketListAdapter(bucketlistActivity.this, R.layout.activity_bucket_list_item, bucketList));
                    Listview.setOnItemClickListener(new ListViewItemClicked());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(bucketlistActivity.this, "Database connection went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bucketlist);

        // TODO: find out if I can easily can create a menu class
        Button btnProfile = findViewById(R.id.btn_profile);
        Button btnSearch = findViewById(R.id.btn_search);

        btnProfile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(bucketlistActivity.this, ProfileActivity.class));
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(bucketlistActivity.this, SearchActivity.class));
            }
        });

        FloatingActionButton btnAddItem = findViewById(R.id.btn_add_item);

        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(bucketlistActivity.this, AddingItemActivity.class));
            }
        });

    }

    private class ListViewItemClicked implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            bucketListItem clickedItem = (bucketListItem) adapterView.getItemAtPosition(i);
            Intent intent = new Intent(bucketlistActivity.this, bucketListItemActivity.class);
            intent.putExtra("CLICKED_ITEM", (Serializable) clickedItem);
            startActivity(intent);
        }
    }
}
