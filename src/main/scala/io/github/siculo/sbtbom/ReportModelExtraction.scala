package io.github.siculo.sbtbom

import io.github.siculo.sbtbom.ReportModel._
import sbt._
import sbt.librarymanagement.ModuleReport

object ReportModelExtraction {
  implicit class UpdateReportOps(updateReport: UpdateReport) {
    def asDependencyReportForConfiguration(configuration: Configuration): DependencyReport =
      configurationReports(configuration.dependencyConfigurations).foldLeft(DependencyReport()) {
        case (collected, configurationReport) =>
          collected + configurationReport.asDependencyReport
      }

    private def configurationReports(configuration: Seq[Configuration]): Seq[ConfigurationReport] =
      configuration.flatMap(updateReport.configuration(_))
  }

  implicit class ConfigurationOps(configuration: Configuration) {
    def dependencyConfigurations: Seq[Configuration] = {
      configuration match {
        case Test =>
          Seq(Test, Runtime, Compile)
        case IntegrationTest =>
          Seq(IntegrationTest, Runtime, Compile)
        case Runtime =>
          Seq(Runtime, Compile)
        case Compile =>
          Seq(Compile)
        case Provided =>
          Seq(Provided)
        case anyOtherConfiguration: Configuration =>
          Seq(anyOtherConfiguration)
        case _ =>
          Seq()
      }
    }
  }

  implicit class ConfigurationReportOps(configuratioReport: ConfigurationReport) {
    def asDependencyReport: DependencyReport =
      DependencyReport(dependencies = configuratioReport.modules.map(_.asDependency))
  }

  /*
    moduleReport.extraAttributes found keys are:
      - "info.apiURL"
      - "info.versionScheme"
   */
  implicit class ModuleReportOps(moduleReport: ModuleReport) {
    def asDependency: Dependency = {
      Dependency(
        group = moduleReport.module.organization,
        name = moduleReport.module.name,
        version = moduleReport.module.revision,
        modified = false,
        licenses = moduleReport.licenses.map {
          case (name, mayBeUrl) =>
            License(name, mayBeUrl)
        }
      )
    }
  }
}
