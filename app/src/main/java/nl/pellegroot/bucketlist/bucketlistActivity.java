package nl.pellegroot.bucketlist;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;

public class bucketlistActivity extends AppCompatActivity {

private FirebaseAuth mAuth;
private FirebaseUser curUser;
private String curUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bucketlist);

        Intent intent = getIntent();

        mAuth = FirebaseAuth.getInstance();
        curUser = mAuth.getCurrentUser();
        curUserId = curUser.getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("Users");

        // TODO: get the users bucketlist and load it into the screen, using a adapter


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
