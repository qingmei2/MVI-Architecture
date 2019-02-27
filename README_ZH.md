# MVI-Rhine

#### [English Documentation](https://github.com/qingmei2/MVI-Rhine) | 中文文档

## 概述

这个 **Github客户端** 的Android项目是基于 **MVI** (Model-View-Intent) 模式进行开发的，项目整体 **业务逻辑** 和 **UI的交互逻辑** 全部交由 **RxJava2** 进行串联。

**MVI** 架构旨在使用  **响应式** 和 **函数式编程** （Reactive && Functional Programming）， 这个架构的两个主要组件，`View`和`ViewModel`可以看作是函数，两者间通过`RxJava`相互输入和输出：

![](https://github.com/qingmei2/MVI-Rhine/blob/master/screenshots/mvi_detail.png)

## 三方组件

### Android 官方架构组件 Jetpack

* [Lifecycle: Create a UI that automatically responds to lifecycle events.](https://developer.android.com/topic/libraries/architecture/lifecycle)

* [ViewModel: Store UI-related data that isn't destroyed on app rotations. Easily schedule asynchronous tasks for optimal execution.](https://developer.android.com/topic/libraries/architecture/viewmodel)

* [Room: Access your app's SQLite database with in-app objects and compile-time checks.](https://developer.android.com/topic/libraries/architecture/room)

* [Navigation: Handle everything needed for in-app navigation.](https://developer.android.com/topic/libraries/architecture/navigation/)

* [Paging: Makes it easier for you to load data gradually and gracefully within your app's RecyclerView.](https://developer.android.com/topic/libraries/architecture/paging/)

### 网络请求

* [Retrofit2: Type-safe HTTP client for Android and Java by Square, Inc.](https://github.com/square/retrofit)

* [OkHttp: An HTTP+HTTP/2 client for Android and Java applications.](https://github.com/square/okhttp)

### 依赖注入

* [Kodein-DI: Painless Kotlin Dependency Injection](https://github.com/Kodein-Framework/Kodein-DI)

### 响应式库

* [RxKotlin: RxJava bindings for Kotlin](https://github.com/ReactiveX/RxKotlin)

* [RxJava2: A library for composing asynchronous and event-based programs using observable sequences for the Java VM](https://github.com/ReactiveX/RxJava)

* [RxAndroid: RxJava bindings for Android](https://github.com/ReactiveX/RxAndroid)

* [RxBinding: RxJava binding APIs for Android's UI widgets.](https://github.com/JakeWharton/RxBinding)

* [AutoDispose: Automatic binding+disposal of RxJava 2 streams.](https://github.com/uber/AutoDispose)

### 函数式库

* [Arrow: Functional companion to Kotlin's Standard Library.](https://arrow-kt.io/)

### 其它

* [Glide: An image loading and caching library for Android focused on smooth scrolling](https://github.com/bumptech/glide)

* [Timber: A logger with a small, extensible API which provides utility on top of Android's normal Log class.](https://github.com/JakeWharton/timber)

## 屏幕截图

<div align:left;display:inline;>
<img width="300" height="540" src="https://github.com/qingmei2/MVI-Rhine/blob/master/screenshots/login.png"/>
<img width="300" height="540" src="https://github.com/qingmei2/MVI-Rhine/blob/master/screenshots/home.png"/>
</div>

<div align:left;display:inline;>
<img width="300" height="540" src="https://github.com/qingmei2/MVI-Rhine/blob/master/screenshots/repos.png"/>
<img width="300" height="540" src="https://github.com/qingmei2/MVI-Rhine/blob/master/screenshots/me.png"/>
</div>

## 感谢

:art: 项目中的UI设计部分参考了 [gitme](https://github.com/flutterchina/gitme) .

:star: 感谢 [oldergod/android-architecture](https://github.com/oldergod/android-architecture) 项目对本项目的指导性作用.

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
