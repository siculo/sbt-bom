package io.github.siculo.sbtbom.model

import sbt.librarymanagement.ModuleReport

case class Module(group: String,
                  name: String,
                  version: String,
                  modified: Boolean,
                  licenses: Seq[License])

object Module {
  def fromModuleReports(reports: Seq[ModuleReport]): Seq[Module] =
    reports.map(Module.fromModuleReport)

  /*
    moduleReport.extraAttributes found keys are:
      - "info.apiURL"
      - "info.versionScheme"
   */
  def fromModuleReport(moduleReport: ModuleReport): Module =
    Module(
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