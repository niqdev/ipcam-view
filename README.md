# ipcam-view ![ipcam-view](images/logo.png)

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-ipcam--view-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/3358)

> work in progress!

Android MJPEG video streaming made simple!

A wrapper around the well known [SimpleMjpegView](https://bitbucket.org/neuralassembly/simplemjpegview) and [android-camera-axis](https://code.google.com/archive/p/android-camera-axis/) libraries.

### Demo app

<img src="images/screenshot-main.png" alt="main" height="600" />
<img src="images/screenshot-default.png" alt="default" height="600" />

<img src="images/screenshot-settings.png" alt="settings" height="600" />

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
Mjpeg.newInstance()
  .credential("USERNAME", "PASSWORD")
  .open("IPCAM_URL.mjpg")
  .subscribe(inputStream -> {
      mjpegView.setSource(inputStream);
      mjpegView.setDisplayMode(DisplayMode.BEST_FIT);
      mjpegView.showFps(true);
  });
```

### Gradle dependency
```
repositories {
    jcenter()
}
dependencies {
    compile 'com.github.niqdev:mjpeg-view:0.1.0'
}
```

### Development
Download Android NDK:
* [manually](http://developer.android.com/ndk/downloads/index.html#download)
* in Android Studio: File > Other Settings > Default Project Structure > download NDK 

Compile manually (verify your paths)
```bash
$ chmod a+x compileJni.sh
$ ./compileJni.sh
```

TODO
- [x] Default support by `android-camera-axis`
- [ ] Native support by `SimpleMjpegView`
- [ ] Handle credential
