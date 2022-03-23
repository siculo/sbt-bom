ThisBuild / organization := "sbtBom"
ThisBuild / organizationName := "SBT BOM"
ThisBuild / version := "0.3.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.12.8"
ThisBuild / homepage := Some(url("https://github.com/siculo/sbt-bom"))

lazy val root = (project in file("."))
  .enablePlugins(ScriptedPlugin)
  .settings(
    name := "sbt-bom",
    sbtPlugin := true,
    libraryDependencies ++= Dependencies.library,
    scriptedLaunchOpts := {
      scriptedLaunchOpts.value ++ Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
    },
    scriptedBufferLog := false,
    dependencyOverrides += "org.typelevel" %% "jawn-parser" % "0.14.1"
  )
