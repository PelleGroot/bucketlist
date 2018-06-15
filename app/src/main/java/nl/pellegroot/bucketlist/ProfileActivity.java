package nl.pellegroot.bucketlist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // TODO: create an edit profile option, to add name, picture and stuff

        Button btnBucketlist = findViewById(R.id.btn_bucketlist);
        Button btnSearch = findViewById(R.id.btn_search);
        Button btnSignOut = findViewById(R.id.btn_sign_out);

        btnBucketlist.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, bucketlistActivity.class));
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, SearchActivity.class));
            }
        });

        TextView profileName = (TextView) findViewById(R.id.txt_profile_name);

        profileName.setText("User's Name");

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            }
        });
    }
}
