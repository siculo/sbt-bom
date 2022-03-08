ThisBuild / organization := "sbtBom"
ThisBuild / organizationName := "SBT BOM"
ThisBuild / version := "0.2.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.12.8"

lazy val root = (project in file("."))
  .enablePlugins(ScriptedPlugin)
  .settings(
    name := "sbt-bom",
    sbtPlugin := true,
    libraryDependencies ++= Dependencies.library,
    scriptedLaunchOpts += ("-Dplugin.version=" + version.value),
    scriptedBufferLog := false,
    dependencyOverrides += "org.typelevel" %% "jawn-parser" % "0.14.1"
  )
