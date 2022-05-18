package io.github.siculo.sbtbom

object ReportModel {
  case class License(name: String, url: Option[String])

  case class Dependency(group: String,
                        name: String,
                        version: String,
                        modified: Boolean,
                        licenses: Seq[License])

  case class DependencyReport(dependencies: Seq[Dependency] = Seq.empty) {
    def +(that: DependencyReport): DependencyReport = DependencyReport(this.dependencies ++ that.dependencies)
  }
}
