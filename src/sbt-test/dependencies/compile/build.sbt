lazy val root = (project in file("."))
  .settings(
    name := "dependencies",
    version := "0.1",
    libraryDependencies ++= Dependencies.library,
    bomFileName := "bom.xml",
    dependencyReportFileName := Some("report.json"),
    scalaVersion := "2.12.8"
  )
