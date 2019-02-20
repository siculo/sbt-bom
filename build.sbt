import Dependencies._

lazy val root = (project in file("."))
  .settings(
    name := "sbt-bom",
    organization := "sbtBom",
    organizationName := "SBT BOM",
    version := "0.1.0-SNAPSHOT",
    sbtPlugin := true,
    scalaVersion := "2.12.8",
    libraryDependencies ++= Dependencies.library,
    scriptedBufferLog := false
  )
