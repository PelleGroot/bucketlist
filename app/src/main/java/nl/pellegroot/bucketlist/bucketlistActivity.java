package nl.pellegroot.bucketlist;

import android.arch.lifecycle.ViewModelStore;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class bucketlistActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bucketlist);

        Intent intent = getIntent();

        final Button btnProfile = findViewById(R.id.btn_profile);
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
    }


}
