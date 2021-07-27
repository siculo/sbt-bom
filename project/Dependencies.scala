import sbt._

object Dependencies {
  lazy val library = Seq(
    "org.cyclonedx" % "cyclonedx-core-java" % "1.1.2",
    "org.scalatest" %% "scalatest" % "3.0.5" % Test
  )
}
