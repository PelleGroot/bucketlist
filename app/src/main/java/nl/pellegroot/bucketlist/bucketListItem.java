package nl.pellegroot.bucketlist;

import java.io.Serializable;

public class bucketListItem implements Serializable{
    public String itemId;
    public String name;
    public String description;
    public String location;
    public String photoUri;
    public Boolean activityDone;
    public int rating;
    public float price;


    public bucketListItem() {
        this.name = "Activity";
        this.description = "description";
//        this.location = null;
        this.photoUri = null;
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

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

}
