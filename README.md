# Color Spotter

This is an Android application that let the user know which color is present in the frame of his device camera. The portion taken into consideration is a small part of the camera feed, highlighted by an overlay. The current frame is analyzed constantly at fixed intervals and color informations are updated accordingly.
It is also possible to see which colors were previously acquired and keep only the relevant ones.

<img src="https://drive.google.com/uc?export=view&id=1BEyfX_XMy0shiUYlOudps3LGH5ymKgtZ" width="250" /> <img src="https://drive.google.com/uc?export=view&id=1B52KgQ6K2fiENSEB303ASQW1ngZpQVsY" width="250" />


## Technical details

The project is divided into following modules:

- *app*: main module
- *common*: module for common resources, shared throughout the project
- *feature_camera_feed*: module for camera feed related tasks
- *feature_user_color_history*: module dedicated to color history management
- *navigation*: separated module to gather navGraph routes in a single place

Architecture is following MVI pattern.

Color quantization is done in *GetTargetAreaColorUseCaseImpl* class. Starting from the provided Bitmap and target radius, a circle area spanning from the center of the frame is analyzed. For every pixel, his red, green and blue channels summarized incrementally into separate variables; the averages of these three channels are going to identify the resulting color. From that, a simple request to [The Color API](https://www.thecolorapi.com/) is going to provide the color name shown to the final user.

Moving to color history feature, the result of every frame analysis is stored in a local database. From the apposite view, the user is able to see the whole list ordered chronologically or alphabetically, together with the possibility to delete color entries one by one.

Libraries used are as follows:

- [Jetpack Compose](https://developer.android.com/jetpack/compose) for UI
- [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) and [Kotlin Flow](https://kotlinlang.org/docs/flow.html) for async tasks
- [Retrofit](https://square.github.io/retrofit/), a type-safe HTTP client
- [Room DB](https://developer.android.com/training/data-storage/room) as local database
- [Koin](https://insert-koin.io/) as dependency injection framework
- [Mockito](https://site.mockito.org/), a mocking framework for unit tests
- [Turbine](https://github.com/cashapp/turbine), a testing library for Kotlin Flow

## Todos

A couple of things left to do:

1. Complete *GetTargetAreaColorUseCaseImpl*: this use case is not fully tested, I need to find a way to test Bitmaps color retrieval in isolation;
2. Camera Preview landscape UI: even though configuration changes are handled gracefully, the UI for the camera preview in landscape mode should be revisited a little bit.

## Additional info

- Android SDK target: **35 (Android 15)**
- minimum Android SDK supported: **28 (Android 9)**
- Kotlin version: **2.0.21**
- Android Gradle plugin version: **8.7.2**
