ThisBuild / organization := "io.github.siculo"
ThisBuild / version := "0.3.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.12.8"
ThisBuild / organizationName := "Siculo"
ThisBuild / organizationHomepage := Some(url("https://github.com/siculo"))
ThisBuild / description := "SBT plugin to generate CycloneDx SBOM files"
ThisBuild / licenses := List("MIT License" -> new URL("https://opensource.org/licenses/MIT"))
ThisBuild / homepage := Some(url("https://github.com/example/project"))

lazy val root = (project in file("."))
  .enablePlugins(ScriptedPlugin)
  .settings(
    name := "sbt-bom",
    sbtPlugin := true,
    libraryDependencies ++= Dependencies.library,
    scriptedLaunchOpts := {
      scriptedLaunchOpts.value ++ Seq("-Xmx1024M", "-Dplugin.version=" + version.value, "-Dplugin.organization=" + organization.value)
    },
    scriptedBufferLog := false,
    dependencyOverrides += "org.typelevel" %% "jawn-parser" % "0.14.1"
  )

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/siculo/sbt-bom"),
    "scm:git@github.com:siculo/sbt-bom.git"
  )
)
ThisBuild / developers := List(
  Developer(
    id    = "siculo",
    name  = "Fabrizio Di Giuseppe",
    email = "siculo.github@gmail.com",
    url   = url("https://github.com/siculo")
  )
)

ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishTo := {
  val nexus = "https://s01.oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
ThisBuild / publishMavenStyle := true
