# ipcam-view ![ipcam-view](images/logo.png)

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-ipcam--view-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/3358)
[![Download](https://api.bintray.com/packages/niqdev/maven/mjpeg-view/images/download.svg)](https://bintray.com/niqdev/maven/mjpeg-view/_latestVersion)

Android MJPEG video streaming made simple!

A wrapper library around the well known [SimpleMjpegView](https://bitbucket.org/neuralassembly/simplemjpegview) and [android-camera-axis](https://code.google.com/archive/p/android-camera-axis/) projects.

If you have problem to identify your IpCam url, please follow this [link](https://github.com/niqdev/ipcam-view/wiki)

### Features
- [x] Default support by `android-camera-axis`
- [ ] Native support by `SimpleMjpegView`
- [x] Handle credentials
- [x] Multiple camera in one activity
- [x] Snapshot
- [x] Flip image
- [ ] Video recording
- [x] Custom appearance

### Gradle dependency
```java
repositories {
    jcenter()
}
dependencies {
    compile 'com.github.niqdev:mjpeg-view:1.4.0'
}
```

### Demo app

<img src="images/screenshot-main.png" alt="main" height="600" /> <img src="images/screenshot-default.png" alt="default" height="600" />

<img src="images/screenshot-two-camera.png" alt="two-camera" height="600" /> <img src="images/screenshot-snapshot.png" alt="snapshot" height="600" />

<img src="images/screenshot-custom-appearance.png" alt="custom-appearance" height="600" /> <img src="images/screenshot-settings.png" alt="settings" height="600" />


<a href='https://play.google.com/store/apps/details?id=com.github.niqdev.ipcam&utm_source=global_co&utm_medium=prtnr&utm_content=Mar2515&utm_campaign=PartBadge&pcampaignid=MKT-Other-global-all-co-prtnr-ap-PartBadge-Mar2515-1'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/apps/en-play-badge.png' width="200"/></a>

### Usage

Add to your layout: [example](app/src/main/res/layout/activity_ipcam_default.xml)
```java

<RelativeLayout
  xmlns:android="http://schemas.android.com/apk/res/android"
  // ADD THIS
  xmlns:stream="http://schemas.android.com/apk/res-auto"
  ...>

    <com.github.niqdev.mjpeg.MjpegSurfaceView
      android:id="@+id/VIEW_NAME"
      android:layout_width="match_parent"
      android:layout_height="match_parent"
      stream:type="stream_default OR stream_native" />

</RelativeLayout>
```

Read stream in your activity/fragment: [example](app/src/main/java/com/github/niqdev/ipcam/IpCamDefaultActivity.java)
```java
int TIMEOUT = 5; //seconds

Mjpeg.newInstance()
  .credential("USERNAME", "PASSWORD")
  .open("IPCAM_URL.mjpg", TIMEOUT)
  .subscribe(inputStream -> {
      mjpegView.setSource(inputStream);
      mjpegView.setDisplayMode(DisplayMode.BEST_FIT);
      mjpegView.showFps(true);
  });
```

### Customize appearance

To get a transparent background for the surface itself (while stream is loading) as well as for the stream background
```java
mjpegView.setTransparentBackground();
// OR
stream:transparentBackground="true"
```

To hide the MjpegView later, you might need to reset the transparency due to internal behaviour of applying transparency
```java
mjpegView.resetTransparentBackground();
```

To set other colors than transparent, be aware that they will only be applied on a running stream i.e. you can't change the color of the surface itself which you will see while the stream is loading

Note that it only works when `transparentBackground` is not set to `true` and that you are not able to directly set transparent background color here
```java
mjpegView.setCustomBackgroundColor(Color.DKGRAY);
// OR
stream:backgroundColor="@android:color/darker_gray"
```

To change the colors of the fps overlay
```java
mjpegView.setFpsOverlayBackgroundColor(Color.DKGRAY);
mjpegView.setFpsOverlayTextColor(Color.WHITE);
```

To clear the last frame since the canvas keeps the current image even if you stop the stream, e.g. hide/show
```java
mjpegView.clearStream();
```

To flip the image
```java
mjpegView.flipHorizontal(true);
mjpegView.flipVertical(true);
```

### Apps that use this library
* [OpenWebNet Android](https://github.com/openwebnet/openwebnet-android)
* [TankDroid](https://github.com/bmachek/TankDroid)

You are welcome to add your app to the list!

### Development
Download Android NDK:
* [manually](http://developer.android.com/ndk/downloads/index.html#download)
* in Android Studio: File > Other Settings > Default Project Structure > download NDK 

Compile manually (verify your paths)
```bash
$ chmod a+x compileJni.sh
$ ./compileJni.sh
```
