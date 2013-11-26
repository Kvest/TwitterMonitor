TwitterMonitor
==============

Used libraries:
plugins/volley - network library
plugins/appcompat - implements support of the Action Bar for Android 2.3
libs/gson-2.2.4.jar - library for working with JSON
libs/android-support-v4.jar - implements support of Fragments and so on for Android 2.3

For building the project just import project into Your IDE and add described libraries to it. plugins/volley and plugins/appcompat should be added as library modules.

Interaction with Twitter API:
To get tweets from Tweeter the application uses a "search" request.
Because Twitter API 1.0 now is not available, it should be used Twitter API 1.1. A "Search" request from API 1.1 requires authentication. 
But it is enough to use "Application-only authentication"(See https://dev.twitter.com/docs/auth/application-only-auth).

Ways to improve application:
- Add an access token validation
- Add a full handling of network errors
