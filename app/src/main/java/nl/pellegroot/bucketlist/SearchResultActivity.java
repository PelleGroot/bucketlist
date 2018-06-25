package nl.pellegroot.bucketlist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;

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
        ArrayList<bucketListItem> activities = intent.getParcelableArrayListExtra("ACTIVITIES");

        ListView searchResultList = (ListView) findViewById(R.id.lv_search_history);
        if(activities != null) {
            searchResultList.setAdapter(new searchResultAdapter(SearchResultActivity.this, R.layout.search_adapter_item, activities));
            searchResultList.setOnItemClickListener(new ListViewResultItemClicked());
        }
    }

    private class ListViewResultItemClicked implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            bucketListItem clickedItem = (bucketListItem) adapterView.getItemAtPosition(i);
            Intent intent = new Intent(SearchResultActivity.this, ResultItemActivity.class);
            intent.putExtra("CLICKED_ITEM", (Serializable) clickedItem);
            startActivity(intent);
        }
    }
}
