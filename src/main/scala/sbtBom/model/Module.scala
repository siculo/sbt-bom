package sbtBom.model

import sbt.librarymanagement.ModuleReport

import java.io.File

case class Module(group: String,
                  name: String,
                  version: String,
                  modified: Boolean,
                  licenseIds: Seq[LicenseId] = Seq(),
                  file: Option[File] = None)

object Module {
  def apply(moduleReport: ModuleReport): Module =
    Module(
      moduleReport.module.organization,
      moduleReport.module.name,
      moduleReport.module.revision,
      modified = false,
      licenseIds = moduleReport.licenses.map(LicenseId(_)),
      file = None
    )
}
