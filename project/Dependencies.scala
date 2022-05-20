import sbt._

object Dependencies {
  val circeVersion = "0.14.1"
  lazy val library: Seq[ModuleID] = Seq(
    "org.cyclonedx" % "cyclonedx-core-java" % "7.1.0",
    "org.scalatest" %% "scalatest" % "3.2.9" % Test,
    "org.scalamock" %% "scalamock" % "5.1.0" % Test
  ) ++ Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % circeVersion)
}
