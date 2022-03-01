package sbtBom.model

import sbt.{Configuration, UpdateReport}
import sbt.librarymanagement.ConfigurationReport

case class Modules(all: Seq[Module] = Seq()) {
  def :+(module: Module) = new Modules(all :+ module)
}

object Modules {
  def apply(report: UpdateReport, configuration: Configuration): Modules =
    Modules(
      report.configuration(configuration)
        .map(mapDependencies)
        .getOrElse(Seq[Module]())
    )

  private def mapDependencies(configurationReport: ConfigurationReport): Seq[Module] =
    configurationReport.modules.map(Module(_))
}
