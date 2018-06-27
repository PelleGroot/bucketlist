# Bucketlist
## Description
This app will allow you to keep track of your bucketlist, add new items on the list and let you check them, when you've done it.
When you have done an item you will also get the opportunity to write your experience, and add a picture.

Using this app, you will keep track of your bucketlist, have a clear overview, add item to the list and check them off. You will be able to set reminders for your items and share the items with friends.
The things on your bucket list does not have to be exciting skydiving/bungiejumping kind of things but it acn also just be a pub you want to visit. That is when the location function comes in handy. You can pin a location to an item to show what and where the item is located.
This is done through the google maps api. Also, you can get suggestions on bucketlist items when creating a new item.

## Design

The login is built upon the Firebase authentication service. Enabling the user to use sign in through google or using an email and password.

After the login, the user gets send to the main screen which is the bucketlist. In this screen the user can add bucketlist items themselves. These items are of the class BucketlistItem and these items are saved in the Firebase database.
The database is built upon the userID and is read and write only to that user.

The users can also use the search page to find restaurants, point of interest, accommodations or activities near them. This part uses the API of Tour-Pedia to get the info needed.
The result of this search can be viewed and the items can be added to their own bucketlist.

### Classes/Modules
AddingItemActivity - Adding a item to the bucketlist
BucketlistActivity - The bucketlist itself
BucketListAdapter - Adapter which fills the BucketlistActivity ListView with items from the Firebase database
BucketListItem - Dataclass, see Data for more
BucketListItemActivity - Details of an item within the bucketlist, which can also contain a photo which is stored in the Firebase storage
CreateAccountActivity - Create an account for the app using email and a password
ItemLocationMapsActivity - Using the Maps SDK, the items can have a location pinned to them
LoginActivity - Login page using the Firebase Authentication
ProfileActivity - The profile page of the user, this is also where the users can sign out
ResultItemActivity - The details of a selected searched item
SearchActivity - The page where you can search for other activities in the neighbourhood using the Tour-Pedia API
SearchRequest - The request that is made to the API to get all the activities
SearchResultActivity - The list of items that has been requested from the API
SearchResultAdapter - The adapter that is being used to show the searched item into the searchActivityResult ListView

### Data

BucketListItem
-------------|
String - name
String - Description
location - location
Uri - Photo
boolean - ActivityDone
String - Lat
String - Lng

## Challenges

Most of the challanges I had were solved by googling a lot and brainstorming about the isues. The biggest issues was that my API research came a bit too short. The API I selected at first isn't free and that is why I had to choose another API. This API was a bit trickier to work with because my design was not calculated on it. But by adding the latitude and longitude to the data class I could work with the API. I just had to write all the location stuff to check if the location was determined by LatLng or PlaceId.
I've also came to the conclusion that my design was a bit too ambitious for the time we had. That is why I had to choose to drop some small functionalities. But I would like to implement them anyway after the assignement is done.

If I had unlimited funds and time, I would have used the other API because it looked very cool and sophisticated. It goes without saying that I would have finished all the functionalities that I designed into the app if I had all the time in the world.
