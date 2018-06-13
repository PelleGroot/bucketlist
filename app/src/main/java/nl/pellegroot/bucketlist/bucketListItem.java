package nl.pellegroot.bucketlist;

import android.location.Location;
import android.media.Image;

import com.google.android.gms.location.places.Place;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;

public class bucketListItem implements Serializable{
    public String itemId;
    public String name;
    public String description;
    public String location;
    public Image photo;
    public Boolean activityDone;
    public int rating;
    public float price;


    public bucketListItem() {
        this.name = "Activity";
        this.description = "description";
//        this.location = null;
        this.photo = null;
        this.activityDone = false;
        this.rating = 0;
        this.price = 0;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Image getPhoto() {
        return photo;
    }

    public void setPhoto(Image photo) {
        this.photo = photo;
    }

    public Boolean getActivityDone() {
        return activityDone;
    }

    public void setActivityDone(Boolean activityDone) {
        this.activityDone = activityDone;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

}
