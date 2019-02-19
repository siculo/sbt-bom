import sbt._

object Dependencies {
  lazy val library = Seq(
    "org.scalatest" %% "scalatest" % "3.0.5" % Test
  )
}
