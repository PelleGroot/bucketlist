package nl.pellegroot.bucketlist;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/*
    this is a data class for the bucketlist items
 */

public class BucketListItem implements Serializable, Parcelable{
    public String name;
    public String description;
    public String location;
    public String photoUri;
    public Boolean activityDone;
    public int rating;
    public String lat;
    public String lng;

    public BucketListItem() {
        this.name = "Activity";
        this.description = "description";
        this.location = null;
        this.photoUri = null;
        this.activityDone = false;
        this.rating = 0;
        this.lat = null;
        this.lng = null;
    }

    public BucketListItem (Parcel parcel) {
        this.name = parcel.readString();
        this.description = parcel.readString();
        this.location = parcel.readString();
        this.photoUri = parcel.readString();
        this.activityDone = false;
        this.rating = 0;
        this.lat = parcel.readString();
        this.lng = parcel.readString();
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

    public String getPhoto() {
        return photoUri;
    }

    public void setPhoto(String photoUri) {
        this.photoUri = photoUri;
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

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Required method to write to Parcel
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(location);
        dest.writeString(photoUri);
        dest.writeString(lat);
        dest.writeString(lng);
    }

    // Method to recreate a Question from a Parcel
    public static Creator<BucketListItem> CREATOR = new Creator<BucketListItem>() {

        @Override
        public BucketListItem createFromParcel(Parcel source) {
            return new BucketListItem(source);
        }

        @Override
        public BucketListItem[] newArray(int size) {
            return new BucketListItem[size];
        }

    };
}
