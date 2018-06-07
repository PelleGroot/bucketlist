package nl.pellegroot.bucketlist;

import android.arch.lifecycle.ViewModelStore;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class bucketlistActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bucketlist);

        Intent intent = getIntent();
    }


}
