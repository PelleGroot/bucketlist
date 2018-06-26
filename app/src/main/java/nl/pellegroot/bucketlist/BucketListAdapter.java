package nl.pellegroot.bucketlist;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

import java.util.ArrayList;

public class bucketListAdapter extends ArrayAdapter{
    public ArrayList<bucketListItem> bucketItemList;
    private FirebaseAuth mAuth;
    private FirebaseUser curUser;
    private String curUserId;

    public bucketListAdapter(@NonNull Context context, int resource, @NonNull ArrayList objects) {
        super(context, resource, objects);
        bucketItemList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.bucketlist_adapter_item, parent, false);
        }

        TextView itemName = (TextView) convertView.findViewById(R.id.BI_itemName);
        final CheckBox itemDone = (CheckBox) convertView.findViewById(R.id.BI_itemDone);

        bucketListItem Item = (bucketListItem) bucketItemList.get(position);

        String buckItemName = Item.getName();
        Boolean buckItemDone = Item.getActivityDone();

        itemName.setText(buckItemName);
        itemDone.setChecked(buckItemDone);

        // set the connection to the DB
        mAuth = FirebaseAuth.getInstance();
        curUser = mAuth.getCurrentUser();
        curUserId = curUser.getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference userRef = database.getReference("Users").child(curUserId).child("bucketlist");

        // create a query to get right DB item for the onclick listener
        final Query clickedItemFromDB = userRef.orderByChild("name").equalTo(Item.getName());

        itemDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Boolean isChecked = itemDone.isChecked();
                clickedItemFromDB.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot ItemFromDB: dataSnapshot.getChildren()){
                            ItemFromDB.getRef().child("activityDone").setValue(isChecked);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("DB error", "Database error, try again!");
                    }
                });
            }
        });

        return convertView;
    }
}
