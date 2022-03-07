lazy val root = (project in file("."))
  .enablePlugins(ScriptedPlugin)
  .settings(
    name := "sbt-bom",
    organization := "sbtBom",
    organizationName := "SBT BOM",
    version := "0.2.0",
    sbtPlugin := true,
    scalaVersion := "2.12.8",
    libraryDependencies ++= Dependencies.library,
    scriptedLaunchOpts += ("-Dplugin.version=" + version.value),
    scriptedBufferLog := false,
    dependencyOverrides += "org.typelevel" %% "jawn-parser" % "0.14.1"
  )
