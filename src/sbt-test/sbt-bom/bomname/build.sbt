lazy val root = (project in file("."))
  .settings(
    targetBomFile := target.value / "bom.xml",
    name := "dependencies",
    version := "0.1",
    libraryDependencies ++= Dependencies.library,
    scalaVersion := "2.12.8"
  )
