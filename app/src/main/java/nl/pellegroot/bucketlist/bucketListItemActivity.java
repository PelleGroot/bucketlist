package nl.pellegroot.bucketlist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class bucketListItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bucket_list_item);
    }
    // TODO: get the bucketlistItems from the database.child(curUserId)
}
