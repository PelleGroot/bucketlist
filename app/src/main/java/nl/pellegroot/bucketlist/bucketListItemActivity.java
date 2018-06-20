package nl.pellegroot.bucketlist;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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

public class bucketListItemActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser curUser;
    private String curUserId;
    private bucketListItem clickedItem;
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

        mAuth = FirebaseAuth.getInstance();
        curUser = mAuth.getCurrentUser();
        curUserId = curUser.getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference userRef = database.getReference("Users").child(curUserId).child("bucketlist");

        storage = FirebaseStorage.getInstance();
        storeRef = storage.getReference();

        Intent intent = getIntent();
        clickedItem = (bucketListItem) intent.getSerializableExtra("CLICKED_ITEM");
        clickedItemFromDB = userRef.orderByChild("name").equalTo(clickedItem.getName());
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
        toolbar.inflateMenu(R.menu.menu);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getItemId()==R.id.menu_edit)
                {
                    Intent intent = new Intent(bucketListItemActivity.this, AddingItemActivity.class);
                    intent.putExtra("CLICKED_ITEM", (Serializable)  clickedItem);
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
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                return false;
            }
        });

        final TextView itemName = (TextView) findViewById(R.id.bucket_item_name);
        TextView itemDescription = (TextView) findViewById(R.id.bucket_item_description);
        CheckBox itemDone = (CheckBox) findViewById(R.id.bucket_item_done);
        photo = (ImageView) findViewById(R.id.photo_activity);

        itemName.setText(clickedItem.getName());
        itemDescription.setText(clickedItem.getDescription());
        itemDone.setChecked(clickedItem.getActivityDone());

        if(clickedItem.getPhoto() != null){
            Uri photoUri = Uri.parse(clickedItem.getPhoto());
            setPhotoInActivity(photoUri);
        }

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

        ImageButton locationItem = (ImageButton) findViewById(R.id.location_icon);
        locationItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Go to the location on google maps
                Intent intent1 = new Intent(bucketListItemActivity.this, ItemLocationMapsActivity.class);
                intent1.putExtra("LOCATION_ID", clickedItem.getLocation());
                startActivity(intent1);
            }
        });

        Button addPhoto = (Button) findViewById(R.id.btn_add_photo);
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePhoto();
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

    private void choosePhoto(){
        Intent photoIntent = new Intent();
        photoIntent.setType("image/*");
        photoIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(photoIntent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent photoIntent) {
        super.onActivityResult(requestCode, resultCode, photoIntent);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && photoIntent != null && photoIntent.getData() != null )
        {
            filePath = photoIntent.getData();
            uploadPhoto();
        }
    }

    private void uploadPhoto(){
        final StorageReference itemPhoto = storeRef.child(curUserId).child(clickedItemId);
        itemPhoto.putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        itemPhoto.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                photoUrl = uri;
                                saveUriInDb();
                            }
                        });
                        Toast.makeText(bucketListItemActivity.this, "Upload succesful!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(bucketListItemActivity.this, "Uploading failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void saveUriInDb(){
        clickedItemFromDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ItemFromDB: dataSnapshot.getChildren()){
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
        Glide
                .with(getBaseContext())
                .load(uri)
                .thumbnail(0.1f)
                .into(photo);
    }
}
