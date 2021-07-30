import sbt._

object Dependencies {
  lazy val library = Seq(
    "org.cyclonedx" % "cyclonedx-core-java" % "5.0.2",
    "org.scalatest" %% "scalatest" % "3.2.9" % Test
  )
}
