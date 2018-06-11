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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class bucketListAdapter extends ArrayAdapter{
    public ArrayList<bucketListItem> bucketItemList;

    private FirebaseAuth mAuth;
    private FirebaseUser curUser;
    private String curUserId;
    private ArrayList bucketList;

    public bucketListAdapter(@NonNull Context context, int resource, @NonNull ArrayList objects) {
        super(context, resource, objects);
        bucketItemList = objects;

        mAuth = FirebaseAuth.getInstance();
        curUser = mAuth.getCurrentUser();
        curUserId = curUser.getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference userRef = database.getReference("Users").child(curUserId).child("bucketlist");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bucketList.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    bucketListItem bucketItem = postSnapshot.getValue(bucketListItem.class);
                    bucketList.add(bucketItem);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("DataBase", "onCancelled: Data was not retrieved");

            }
        });

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.bucketlist_adapter_item, parent, false);
        }

        TextView itemName = (TextView) convertView.findViewById(R.id.BI_itemName);
        CheckBox itemDone = (CheckBox) convertView.findViewById(R.id.BI_itemDone);

        bucketListItem Item = bucketList.get(position);

        // TODO: get the bucketlistItems from the database.child(curUserId)

        return convertView;
    }
}
