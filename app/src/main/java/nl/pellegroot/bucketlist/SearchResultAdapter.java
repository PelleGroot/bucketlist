package nl.pellegroot.bucketlist;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/*
    this is the adapter for the SearchResultActivity
 */

public class SearchResultAdapter extends ArrayAdapter {
    public ArrayList<BucketListItem> bucketItemList;


    public SearchResultAdapter(@NonNull Context context, int resource, @NonNull ArrayList objects) {
        super(context, resource, objects);
        bucketItemList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.search_adapter_item, parent, false);
        }

        TextView itemName = (TextView) convertView.findViewById(R.id.search_itemName);

        BucketListItem Item = (BucketListItem) bucketItemList.get(position);

        String buckItemName = Item.getName();
        itemName.setText(buckItemName);

        return convertView;
    }
}
