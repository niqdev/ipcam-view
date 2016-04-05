#!/bin/bash

JNI_SRC="mjpeg-view/src/main"

rm -fr $JNI_SRC/jniLibs
~/Library/Android/sdk/ndk-bundle/ndk-build -C $JNI_SRC
cp -a $JNI_SRC/libs $JNI_SRC/jniLibs
rm -fr $JNI_SRC/libs $JNI_SRC/obj

echo $JNI_SRC/jniLibs
echo "build success!"