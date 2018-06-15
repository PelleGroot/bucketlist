package nl.pellegroot.bucketlist;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    private String clickedItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bucket_list_item);

        mAuth = FirebaseAuth.getInstance();
        curUser = mAuth.getCurrentUser();
        curUserId = curUser.getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference userRef = database.getReference("Users").child(curUserId).child("bucketlist");

        // TODO: ability to add a photo to the activity
        // TODO: ability to edit item

        Intent intent = getIntent();
        clickedItem = (bucketListItem) intent.getSerializableExtra("CLICKED_ITEM");
        final Query clickedItemFromDB = userRef.orderByChild("name").equalTo(clickedItem.getName());
        clickedItemFromDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ItemFromDB: dataSnapshot.getChildren()){
                    clickedItemId = ItemFromDB.getRef().getKey();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(bucketListItemActivity.this, "Database error, try again!", Toast.LENGTH_SHORT).show();
            }
        });

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitle("TestMenu");
        toolbar.inflateMenu(R.menu.menu);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getItemId()==R.id.menu_edit)
                {
                    Intent intent = new Intent(bucketListItemActivity.this, AddingItemActivity.class);
                    intent.putExtra("CLICKED_ITEM", clickedItem);
                    intent.putExtra("ITEMID", clickedItemId);
                    startActivity(intent);
                }
                else if(menuItem.getItemId()== R.id.menu_delete)
                {
                    final Context context = toolbar.getContext();
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setCancelable(true);
                    builder.setTitle("Warning!");
                    builder.setMessage("Are you sure you want to delete this activity?");
                    builder.setPositiveButton(android.R.string.yes,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    userRef.child(clickedItemId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            startActivity(new Intent(context, bucketlistActivity.class));
                                        }
                                    });
                                }
                            });
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("Dialog stuff", "onClick:  cancel");
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else{
                    // do something
                }
                return false;
            }
        });

        TextView itemName = (TextView) findViewById(R.id.bucket_item_name);
        TextView itemDescription = (TextView) findViewById(R.id.bucket_item_description);
        CheckBox itemDone = (CheckBox) findViewById(R.id.bucket_item_done);

        itemName.setText(clickedItem.getName());
        itemDescription.setText(clickedItem.getDescription());
        itemDone.setChecked(clickedItem.getActivityDone());

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

        //TODO: Sharing makes the android crash. Find a way to share data with others within the app. (maybe create a JSON and be able to send that? and make an activity where you can accept incoming JSON's)
        Button sharing = (Button) findViewById(R.id.btn_share);
        sharing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
//                sendIntent.putExtra("SHARED_DATA", clickedItem);
//                sendIntent.setType("Object/*");
                startActivity(sendIntent);
            }
        });
    }

}
