# ipcam-view

> work in progress!

Android MJPEG video streaming made simple!
A wrapper around the well known [SimpleMjpegView](https://bitbucket.org/neuralassembly/simplemjpegview) and [android-camera-axis](https://code.google.com/archive/p/android-camera-axis/) libraries.

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
```
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

TODO
- [x] Default support with `android-camera-axis`
- [ ] Native support with `SimpleMjpegView`
- [ ] Handle credential