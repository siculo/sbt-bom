package io.github.siculo.sbtbom

import sbt.util.{Level, Logger}

object TestLogger extends Logger {
  override def trace(t: => Throwable): Unit = {}

  override def success(message: => String): Unit = {}

  override def log(level: Level.Value, message: => String): Unit = {}
}