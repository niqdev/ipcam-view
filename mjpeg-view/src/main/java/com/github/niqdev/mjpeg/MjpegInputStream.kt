package com.github.niqdev.mjpeg

import java.io.DataInputStream
import java.io.InputStream

abstract class MjpegInputStream(insputStream: InputStream) : DataInputStream(insputStream)