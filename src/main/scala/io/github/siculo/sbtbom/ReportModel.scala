package io.github.siculo.sbtbom

import play.api.libs.json._

object ReportModel {
  case class License(name: String, url: Option[String])

  case class Dependency(group: String,
                        name: String,
                        version: String,
                        modified: Boolean,
                        licenses: Seq[License],
                        filePaths: Seq[String])

  case class DependencyReport(dependencies: Seq[Dependency] = Seq.empty) {
    def +(that: DependencyReport): DependencyReport = DependencyReport(this.dependencies ++ that.dependencies)
  }

  implicit val licenseFormat: Format[License] = Json.format[License]
  implicit val dependencyFormat: Format[Dependency] = Json.format[Dependency]
  implicit val dependencyReportFormat: Format[DependencyReport] = Json.format[DependencyReport]
}
