# maneuver-game
simple Android game made with libGDX

![Screenshot1](/screenshot/1.jpg?raw=true)
![Screenshot1](/screenshot/2.jpg?raw=true)
![Screenshot1](/screenshot/3.jpg?raw=true)

### Running and build requires ###
* JDK6 (JAVA_HOME environment variable must be set to jdk directory)
* Android SDK for building for Android

### How do I get set up? ###

1. `git clone https://github.com/sunr1s3/maneuver-game.git`
2. `cd maneuver-game/maneuver`
3. if needed set `ANDROID_HOME` variable point to your SDK *OR* `touch local.properties` and fill it with sdk.dir=<PATH TO ANDROID SDK>  

Then:

* `./gradlew desktop:run` run on desktop
* `./gradlew android:installDebug` android:run
* `./gradlew desktop:dist` build .jar in `desktop/build/libs`
* `./gradlew android:assembleRelease` build .apk in `android/build/outputs/apk`

Sure you may import Gradle-project to your favourite IDE and use it for running and building.
