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
        CheckBox itemDone = (CheckBox) convertView.findViewById(R.id.BI_itemDone);

        Log.d("DBstuff-within adapter", "getView: " + bucketItemList.get(1).getName());


        bucketListItem Item = (bucketListItem) bucketItemList.get(position);

        String buckItemName = Item.getName();
        Boolean buckItemDone = Item.getActivityDone();
        Log.d("DBstuff-Adapter", "getView: " + Item.getName());
        Log.d("DBstuff-Adapter", "getView: " + Item.getActivityDone());


        itemName.setText(buckItemName);
        itemDone.setChecked(buckItemDone);

        return convertView;
    }
}
