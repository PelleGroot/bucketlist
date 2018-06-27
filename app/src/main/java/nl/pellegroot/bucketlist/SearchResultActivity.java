package nl.pellegroot.bucketlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;

/*
    this activity show the result of the search activity
    this activity shows all the places that you've searched for
 */

public class SearchResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        Button btnBackToSearch = findViewById(R.id.btn_back_to_search);

        btnBackToSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SearchResultActivity.this, SearchActivity.class));
            }
        });

        Intent intent = getIntent();
        ArrayList<BucketListItem> activities = intent.getParcelableArrayListExtra("ACTIVITIES");

        ListView searchResultList = (ListView) findViewById(R.id.lv_search_history);
        if(activities != null) {
            searchResultList.setAdapter(new SearchResultAdapter(SearchResultActivity.this, R.layout.search_adapter_item, activities));
            searchResultList.setOnItemClickListener(new ListViewResultItemClicked());
        }
    }

    private class ListViewResultItemClicked implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            BucketListItem clickedItem = (BucketListItem) adapterView.getItemAtPosition(i);
            Intent intent = new Intent(SearchResultActivity.this, ResultItemActivity.class);
            intent.putExtra("CLICKED_ITEM", (Serializable) clickedItem);
            startActivity(intent);
        }
    }
}
