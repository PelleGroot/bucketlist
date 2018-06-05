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

![Design doc Bucketlist](/images/Bucketlist_app_DesignDoc.png)
