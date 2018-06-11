package nl.pellegroot.bucketlist;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class bucketlistActivity extends AppCompatActivity {

private FirebaseAuth mAuth;
private FirebaseUser curUser;
private String curUserId;
private ArrayList<bucketListItem> bucketList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bucketlist);

        mAuth = FirebaseAuth.getInstance();
        curUser = mAuth.getCurrentUser();
        curUserId = curUser.getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("Users").child(curUserId).child("bucketlist");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    Log.d("DBstuff", "onDataChange: " + postSnapshot.getValue(bucketListItem.class).getName());
                    Log.d("DBstuff", "onDataChange: " + postSnapshot.getValue(bucketListItem.class).getActivityDone());

                    bucketListItem bucketItem = new bucketListItem();
                    bucketItem.setName(postSnapshot.getValue(bucketListItem.class).getName());
                    bucketItem.setActivityDone(postSnapshot.getValue(bucketListItem.class).getActivityDone());
                    bucketList.add(bucketItem);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("DataBase", "onCancelled: Data was not retrieved");

            }
        });
        // TODO: get the users bucketlist and load it into the screen, using a adapter
        ListView Listview = findViewById(R.id.lv_bucketlist);
        Log.d("Adapter stuff", "onCreate: " + bucketList.get(0).getName());
        if(bucketList!= null) {
            Listview.setAdapter(new bucketListAdapter(bucketlistActivity.this, R.layout.activity_bucket_list_item, bucketList));
        }

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


}
