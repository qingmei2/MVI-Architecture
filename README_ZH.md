# MVI-Rhine

#### [English Documentation](https://github.com/qingmei2/MVI-Rhine/blob/master/README.md) | 中文文档

**The MVI architecture using RxJava2 and Android Jetpack.**

## Summary

> This repo is based on an Android ported version of the **Model-View-Intent architecture** and uses RxJava2 to implement the reactive caracteristic of the architecture.

> The **MVI** architecture embraces reactive and functional programming. The two main components of this architecture, the View and the ViewModel can be seen as functions, taking an input and emiting outputs to each other. The View takes input from the ViewModel and emit back intents. The ViewModel takes input from the View and emit back view states. This means the View has only one entry point to forward data to the ViewModel and vice-versa, the ViewModel only has one way to pass information to the View.

![](https://github.com/qingmei2/MVI-Rhine/blob/master/screenshots/mvi_detail.png)

## Libraries

### Android Jetpack

* [Lifecycle: Create a UI that automatically responds to lifecycle events.](https://developer.android.com/topic/libraries/architecture/lifecycle)

* [ViewModel: Store UI-related data that isn't destroyed on app rotations. Easily schedule asynchronous tasks for optimal execution.](https://developer.android.com/topic/libraries/architecture/viewmodel)

* [Room: Access your app's SQLite database with in-app objects and compile-time checks.](https://developer.android.com/topic/libraries/architecture/room)

* [Navigation: Handle everything needed for in-app navigation.](https://developer.android.com/topic/libraries/architecture/navigation/)

* [Paging: Makes it easier for you to load data gradually and gracefully within your app's RecyclerView.](https://developer.android.com/topic/libraries/architecture/paging/)

### Http

* [Retrofit2: Type-safe HTTP client for Android and Java by Square, Inc.](https://github.com/square/retrofit)

* [OkHttp: An HTTP+HTTP/2 client for Android and Java applications.](https://github.com/square/okhttp)

### DI

* [Kodein-DI: Painless Kotlin Dependency Injection](https://github.com/Kodein-Framework/Kodein-DI)

### ReactiveX

* [RxKotlin: RxJava bindings for Kotlin](https://github.com/ReactiveX/RxKotlin)

* [RxJava2: A library for composing asynchronous and event-based programs using observable sequences for the Java VM](https://github.com/ReactiveX/RxJava)

* [RxAndroid: RxJava bindings for Android](https://github.com/ReactiveX/RxAndroid)

* [RxBinding: RxJava binding APIs for Android's UI widgets.](https://github.com/JakeWharton/RxBinding)

* [AutoDispose: Automatic binding+disposal of RxJava 2 streams.](https://github.com/uber/AutoDispose)

### Functional

* [Arrow: Functional companion to Kotlin's Standard Library.](https://arrow-kt.io/)

### Others

* [Glide: An image loading and caching library for Android focused on smooth scrolling](https://github.com/bumptech/glide)

* [Timber: A logger with a small, extensible API which provides utility on top of Android's normal Log class.](https://github.com/JakeWharton/timber)

## ScreenShots

<div align:left;display:inline;>
<img width="300" height="540" src="https://github.com/qingmei2/MVI-Rhine/blob/master/screenshots/login.png"/>
<img width="300" height="540" src="https://github.com/qingmei2/MVI-Rhine/blob/master/screenshots/home.png"/>
</div>

<div align:left;display:inline;>
<img width="300" height="540" src="https://github.com/qingmei2/MVI-Rhine/blob/master/screenshots/repos.png"/>
<img width="300" height="540" src="https://github.com/qingmei2/MVI-Rhine/blob/master/screenshots/me.png"/>
</div>

## Thanks to

:art: The UI design of this project refers to [gitme](https://github.com/flutterchina/gitme).

:star: Thanks for [oldergod/android-architecture](https://github.com/oldergod/android-architecture)'s guidance during development.

## License

    The MVI-Rhine: Apache License

    Copyright (c) 2019 qingmei2

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
