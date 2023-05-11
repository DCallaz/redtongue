# RedTongue
A simplistic WiFi transfer protocol written in java.

RedTonuge allows you to securely and seemlessly transfer files between your devices
over your local network, using a fast network protocol that uses UDP as its
underlying transfer protocol.

## Installation and usage
To use RedTongue, simply clone this repository, and run the `RedTongue.jar` file:
```
java -jar RedTongue.jar
```
Which will start the GUI client.
You can then open the app on another device. Files can be shared only with
devices that are paired, so the client will initially ask to pair as a receiver
or as a sender. Pairing two devices will then allow you to send files between
these devices.

## Other platforms
Currently, RedTongue is only available as a Java JAR executable and an
android application. The JAR executable allows RedTongue to be run on any
operating system, and can be cloned from this repository. For the android
application, see [Android application](https://github.com/DCallaz/RedTongue_Android).
