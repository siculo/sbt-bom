lazy val root = (project in file("."))
  .settings(
    name := "dependencies",
    version := "0.1",
    libraryDependencies ++= Dependencies.library,
    IntegrationTest / bomFileName := "bom.xml",
    IntegrationTest / dependencyReportFileName := Some("report.json"),
    scalaVersion := "2.12.8"
  )
