package nl.pellegroot.bucketlist;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class bucketListItemActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser curUser;
    private String curUserId;
    private bucketListItem clickedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bucket_list_item);

        mAuth = FirebaseAuth.getInstance();
        curUser = mAuth.getCurrentUser();
        curUserId = curUser.getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference userRef = database.getReference("Users").child(curUserId).child("bucketlist");

        // TODO: Get itemId From the intent and load the data from the database
        Intent intent = getIntent();
        clickedItem = (bucketListItem) intent.getSerializableExtra("CLICKED_ITEM");

        Log.d("stuff", "onCreate: " + clickedItem.getName());

        TextView itemName = (TextView) findViewById(R.id.bucket_item_name);
        TextView itemDescription = (TextView) findViewById(R.id.bucket_item_description);
        CheckBox itemDone = (CheckBox) findViewById(R.id.bucket_item_done);

        itemName.setText(clickedItem.getName());
        itemDescription.setText(clickedItem.getDescription());
        itemDone.setChecked(clickedItem.getActivityDone());

        final Query clickedItemFromDB = userRef.orderByChild("name").equalTo(clickedItem.getName());

        itemDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, final boolean b) {
                clickedItemFromDB.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot ItemFromDB: dataSnapshot.getChildren()){
                            ItemFromDB.getRef().child("activityDone").setValue(b);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(bucketListItemActivity.this, "Database error, try again!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
}
