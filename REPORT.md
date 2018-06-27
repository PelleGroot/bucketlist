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
