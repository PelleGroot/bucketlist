package nl.pellegroot.bucketlist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class SearchResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        // TODO: get the searched for items in here using an adapter

        Button btnBackToSearch = findViewById(R.id.btn_back_to_search);

        btnBackToSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SearchResultActivity.this, SearchActivity.class));
            }
        });

        Intent intent = getIntent();
        ArrayList<bucketListItem> activities = intent.getParcelableArrayListExtra("ACTIVITIES");
    }
}
