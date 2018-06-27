package nl.pellegroot.bucketlist;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/*
    this activity show the info of the current user and gives the ability to log out
 */


public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser curUser;
    private String curUserId;
    private ImageView profilePic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // get a connection to the firebase database
        mAuth = FirebaseAuth.getInstance();
        curUser = mAuth.getCurrentUser();
        curUserId = curUser.getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("Users").child(curUserId);

        // set the menu buttons
        Button btnBucketlist = findViewById(R.id.btn_bucketlist);
        Button btnSearch = findViewById(R.id.btn_search);

        btnBucketlist.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, BucketlistActivity.class));
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, SearchActivity.class));
            }
        });

        // set the profile name field
        TextView profileName = (TextView) findViewById(R.id.txt_profile_name);
        profilePic = (ImageView) findViewById(R.id.pic_profile);
        TextView profileEmail = (TextView) findViewById(R.id.txt_email_profile);

        // set the profile info to the fields
        profileName.setText(curUser.getDisplayName());
        profileEmail.setText(curUser.getEmail());
        setPhotoInActivity(curUser.getPhotoUrl());

        // set the sign out button
        Button btnSignOut = findViewById(R.id.btn_sign_out);

        // sign out
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            }
        });
    }
    public void setPhotoInActivity(Uri uri){

        // set the photo in the activity using the glide library
        Glide
                .with(getBaseContext())
                .load(uri)
                .centerCrop()
                .thumbnail(0.1f)
                .into(profilePic);
    }
}
