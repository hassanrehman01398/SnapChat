# Employee of the month
## What is it?
This app was created as part of the last coding assignment in my first year at studying Software Development at NOVI Hogeschool (The Netherlands).  
It is also the first-ever Android app I wrote.
## What does it do?
It's kind of a poor man's Snapchat clone, meaning you can take pictures (of someone) with it, overlay funny stuff (such as hats, ears, beards and the like) and share the result with your friends (or the rest of the world). And that's it.
## Files and permissions
The app uses its own application folder to store .jpg files. It only needs permission for the camera.
## Source material
The app uses (modified versions of) code from the following GitHub projects (thanks!):  
- [Camera2Basic](https://github.com/googlesamples/android-Camera2Basic)  
- [Glide](https://github.com/bumptech/glide)  
- [MotionViews-Android](https://github.com/uptechteam/MotionViews-Android)  
- [Android Gesture Detectors](https://github.com/Almeros/android-gesture-detectors)  
## Building the app
[Secure key signing](https://developer.android.com/studio/publish/app-signing#secure-key) is used in the build.gradle file in the app folder.  
If you do not want to use key signing, just edit that file accordingly and rebuild.  

I hope you have as much fun with it as I did!
## Screenshot
![Screenshot](eotm.jpg)