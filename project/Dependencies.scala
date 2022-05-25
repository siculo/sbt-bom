import sbt._

object Dependencies {
  lazy val library: Seq[ModuleID] = Seq(
    "org.cyclonedx" % "cyclonedx-core-java" % "7.1.0",
    "com.typesafe.play" %% "play-json" % "2.8.2",
    "org.scalatest" %% "scalatest" % "3.2.9" % Test,
    "org.scalamock" %% "scalamock" % "5.1.0" % Test
  )
}
