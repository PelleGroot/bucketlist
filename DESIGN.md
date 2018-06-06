# Design document
This is the design document for the bucketlist app

## Dataclasses

Bucketlist
-------------|
List[activity]
**functions**
Share list

Activity
-------------|
string - name
string - Description
location - location
image - Photo
boolean - ActivityDone
int - Rating
float - price
**functions**
shareActivity
addToBucketlist
setReminder
setActivityDone
setLocation
setPhoto

## External components
* [Google Firebase Authentication and database](https://firebase.google.com/docs/auth/users)
* [Adventure bucketlist API](http://developer.adventurebucketlist.com/?javascript#introduction-to-api)
* [Google places API](https://developers.google.com/places/android-sdk/intro)

## Database
I'm going to use a firebase database to store the users and their bucketlist

* UserId
* Username
* UserPhoto
* UserBucketlist
  * BucketItem
    * ItemName
    * ItemDescription
    * ItemLocation
    * ItemPhoto
    * ItemDone
    * ItemRating

## Activities scheme
Next is a clear overview of the activities inside the app and the functions that are in the activities
![Activities bucketlist](/images/bucketlistActivities.png)

## UI design
For a clickable UI design, visit [this](https://www.fluidui.com/editor/live/project/p_GaQRHQzD5po83MybhQaNpEQLfzozx631)

![Design doc Bucketlist](/images/Bucketlist_app_DesignDoc.png)
