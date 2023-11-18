# GDD-app

## How to contribute

- Pick a task from our [kanban board on trello](https://trello.com/b/kcLkX2WQ/disease-tagging-app)
- Make sure the tests pass on Bitrise.
- Open a Pull Request, or ask to be added to the repository and commit directly to master

## Setting up the development environment

Requirements:

1. Java 17
1. Android Studio
1. Android SDK

Steps:

1. Install Android Studio: https://developer.android.com/studio/ or `brew cask install android-studio`
1. Clone the repository to your computer
1. Open Android Studio, select “Import”, and select the location where
you’ve cloned the app
1. [OPTIONAL] Copy `properties/secrets.sample.properties` to `properties/secrets.properties` and replace the
placeholders
1. Make sure the build succeeds - follow the instructions in the “build
window”, e.g. to install the Android SDK and accept licenses
1. Run the app on an emulator: Follow the app tutorial
(https://developer.android.com/training/basics/firstapp/running-app) ‘s
section Run on an emulator

## Learning Resources

kotlin:  
https://kotlinlang.org/docs/reference/android-overview.html  

android:  
https://developer.android.com/training/basics/firstapp/creating-project  
https://antonioleiva.com/free-guide/  
https://www.raywenderlich.com/library?domain_ids%5B%5D=2  

Good Android tutorials:  
https://www.raywenderlich.com/library?domain_ids%5B%5D=2  

Android course:  
https://developer.android.com/courses  


## Further documentation
Find more documentation in the [doc folder](doc), for example:

* [Application architecture](doc/architecture.md)
* [UI Design](doc/UI-Design.md)
* [Architecture Decision Records](doc/adr)
