# ipcam-view

> work in progress!

Android MJPEG video streaming made simple!

A wrapper around the well known [SimpleMjpegView](https://bitbucket.org/neuralassembly/simplemjpegview) and [android-camera-axis](https://code.google.com/archive/p/android-camera-axis/) libraries.

```java

// add to layout
xmlns:stream="http://schemas.android.com/apk/res-auto"

<com.github.niqdev.mjpeg.MjpegSurfaceView
  android:id="@+id/VIEW_NAME"
  android:layout_width="match_parent"
  android:layout_height="match_parent"
  stream:type="stream_default OR stream_native" />

Mjpeg.newInstance()
  .credential("USERNAME", "PASSWORD")
  .open("IPCAM_URL.mjpg")
  .subscribe(inputStream -> {
      mjpegView.setSource(inputStream);
      mjpegView.setDisplayMode(DisplayMode.BEST_FIT);
      mjpegView.showFps(true);
  });
```

TODO
- [x] Default support with `android-camera-axis`
- [ ] Native support with `SimpleMjpegView`
- [ ] Handle credential