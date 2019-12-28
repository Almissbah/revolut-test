# Revolut Test
one screen test app for revolut interview, the screen has a list of currencies from the endpoint (one per row) fetched every 1 sec.


## App Features
1. The app lists the currencie, Each in a row and updates them every 1 sec.
1. User can tap on currency row and it slides to top and its input becomes the first responder.
2. User can change the amount and the app simultaneously updates the corresponding value for other currencies.

## App architecture
Based on mvvm architecture and repository pattern.

### The app includes the following main components:
 
* A web api service.
* A repository that works with the api service to provide a unified data interface.
* A ViewModel that provides data specific for the UI using RxJava and LiveData.
* The UI, which shows a visual representation of the data in the ViewModel.

### App Packages:
* **data** - contains:
  * **RevoRepository** -  app repository class for handling data.
  * **remote** - contains the api classes needed for making calls to Revolut server, using Retrofit.
* **di** - contains dependency injection classes, using Dagger2.
* **ui** - contains classes needed to display Activity and Fragment.
* **util** - contains app constants and diffUtilsCallback for recycler view optimization.


### App Specs
* Minimum SDK 19.
* Kotlin.
* MVVM Architecture.
* Android Architecture Components (LiveData, Lifecycle, ViewModel, ConstraintLayout)
* Dagger 2 for dependency injection.
* RxJava for making calles every x sec.
* Retrofit 2 for API integration.
* Gson for serialisation.

