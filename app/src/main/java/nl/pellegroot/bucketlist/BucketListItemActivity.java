package nl.pellegroot.bucketlist;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.Serializable;

/*
    this activity shows the item clicked in the bucketlist
 */

public class BucketListItemActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser curUser;
    private String curUserId;
    private BucketListItem clickedItem;
    private String clickedItemId;
    private FirebaseStorage storage;
    private StorageReference storeRef;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    private ImageView photo;
    private Uri photoUrl;
    private Query clickedItemFromDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bucket_list_item);

        // initiate the firebase database connection
        mAuth = FirebaseAuth.getInstance();
        curUser = mAuth.getCurrentUser();
        curUserId = curUser.getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference userRef = database.getReference("Users").child(curUserId).child("bucketlist");

        // initiate connection to firebase storage
        storage = FirebaseStorage.getInstance();
        storeRef = storage.getReference();

        // get the item that was clicked from the intent
        Intent intent = getIntent();
        clickedItem = (BucketListItem) intent.getSerializableExtra("CLICKED_ITEM");
        clickedItemFromDB = userRef.orderByChild("name").equalTo(clickedItem.getName());

        // get the database ID for the item
        clickedItemFromDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ItemFromDB: dataSnapshot.getChildren()){
                    clickedItemId = ItemFromDB.getRef().getKey();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(BucketListItemActivity.this, "Database error, try again!", Toast.LENGTH_SHORT).show();
            }
        });

        // set the fields
        final TextView itemName = (TextView) findViewById(R.id.bucket_item_name);
        TextView itemDescription = (TextView) findViewById(R.id.bucket_item_description);
        CheckBox itemDone = (CheckBox) findViewById(R.id.bucket_item_done);
        photo = (ImageView) findViewById(R.id.photo_activity);

        // set the texts to the fields
        itemName.setText(clickedItem.getName());
        itemDescription.setText(clickedItem.getDescription());
        itemDone.setChecked(clickedItem.getActivityDone());

        // get the photo if there is a photo saved
        if(clickedItem.getPhoto() != null){
            Uri photoUri = Uri.parse(clickedItem.getPhoto());
            setPhotoInActivity(photoUri);
        }

        // set the listener to the checkbox
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
                        Toast.makeText(BucketListItemActivity.this, "Database error, try again!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        ImageButton locationItem = (ImageButton) findViewById(R.id.location_icon);
        locationItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(clickedItem.getLocation() != null) {

                    // If the location ID is available go to the location on google maps
                    Intent intent1 = new Intent(BucketListItemActivity.this, ItemLocationMapsActivity.class);
                    intent1.putExtra("LOCATION_ID", clickedItem.getLocation());
                    startActivity(intent1);
                }
                else if(clickedItem.getLat() != null){

                    // if the Lat Lng id available go to the location on google maps
                    Intent intent1 = new Intent(BucketListItemActivity.this, ItemLocationMapsActivity.class);
                    intent1.putExtra("LOCATION_LAT", clickedItem.getLat());
                    intent1.putExtra("LOCATION_LNG", clickedItem.getLng());
                    startActivity(intent1);
                }
                else{

                    // If both latlng or locationID aren't available, show a Toast message
                    Toast.makeText(BucketListItemActivity.this, "No location found", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // set the functionality to add a photo
        Button addPhoto = (Button) findViewById(R.id.btn_add_photo);
        addPhoto.setOnClickListener(new choosePhoto());

        // Set the sharing functionality to the button
        Button sharing = (Button) findViewById(R.id.btn_share);
        sharing.setOnClickListener(new shareActivity());

        // create a menu for the buttons 'edit' and 'delete'
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getItemId()==R.id.menu_edit)
                {
                    // puts the items database ID and the item itself in the intent to start a edit item activity
                    Intent intent = new Intent(BucketListItemActivity.this, AddingItemActivity.class);
                    intent.putExtra("CLICKED_ITEM", (Serializable)  clickedItem);
                    intent.putExtra("ITEMID", clickedItemId);
                    startActivity(intent);
                }
                else if(menuItem.getItemId()== R.id.menu_delete)
                {
                    // create a confirmation message before actually deleting the item
                    final Context context = toolbar.getContext();
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setCancelable(true);
                    builder.setTitle("Warning!");
                    builder.setMessage("Are you sure you want to delete this activity?");
                    builder.setPositiveButton(android.R.string.yes,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    // if the user confirms, the item gets deleted from the database
                                    userRef.child(clickedItemId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            startActivity(new Intent(context, BucketlistActivity.class));
                                        }
                                    });
                                }
                            });
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // if the users chooses negative, the user returns to the activity
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                return false;
            }
        });
    }

    private class choosePhoto implements View.OnClickListener{
        @Override
        public void onClick(View view){

            // on click, start the choose image proces
            Intent photoIntent = new Intent();
            photoIntent.setType("image/*");
            photoIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(photoIntent, "Select Picture"), PICK_IMAGE_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent photoIntent) {
        super.onActivityResult(requestCode, resultCode, photoIntent);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && photoIntent != null && photoIntent.getData() != null )
        {

            // when a photo is choosen, start the upload function
            filePath = photoIntent.getData();
            uploadPhoto();
        }
    }

    private void uploadPhoto(){

        //create a new storage reference for the photo
        final StorageReference itemPhoto = storeRef.child(curUserId).child(clickedItemId);
        itemPhoto.putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        itemPhoto.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                // Save the photo in the storage and get the URI for the photo to save it in the DB
                                photoUrl = uri;
                                saveUriInDb();
                            }
                        });
                        Toast.makeText(BucketListItemActivity.this, "Upload succesful!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(BucketListItemActivity.this, "Uploading failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void saveUriInDb(){
        clickedItemFromDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ItemFromDB: dataSnapshot.getChildren()){

                    // save the photo Uri to the database item and set the photo in the activity
                    ItemFromDB.getRef().child("photoUri").setValue(photoUrl.toString());
                    setPhotoInActivity(photoUrl);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    public void setPhotoInActivity(Uri uri){

        // set the photo in the activity using the glide library
        Glide
                .with(getBaseContext())
                .load(uri)
                .thumbnail(0.1f)
                .into(photo);
    }

    private class shareActivity implements View.OnClickListener{

        @Override
        public void onClick(View view) {

            // on clicked, share the item through the medium the user chooses as text
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, clickedItem.getName());
            sendIntent.putExtra(Intent.EXTRA_TEXT, clickedItem.getDescription());
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Share activity"));
        }
    }
}
