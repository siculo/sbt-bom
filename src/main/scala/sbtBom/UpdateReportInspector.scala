package sbtBom

import sbt.librarymanagement.{ConfigurationReport, ModuleReport}
import sbt.{Configuration, UpdateReport}
import sbtBom.model.{License, LicenseId, Module, Modules}

class UpdateReportInspector(report: UpdateReport) {
  def dependencies(configuration: Configuration): Modules = {
    val dependencies = report.configuration(configuration).map(mapDependencies).getOrElse(Seq[Module]())
    Modules(dependencies)
  }

  private def mapDependencies(configurationReport: ConfigurationReport): Seq[Module] =
    configurationReport.modules.map(mapDependency)

  private def mapDependency(moduleReport: ModuleReport): Module =
    Module(
      moduleReport.module.organization,
      moduleReport.module.name,
      moduleReport.module.revision,
      modified = false,
      licenses = mapLicenses(mapLicenseIds(moduleReport.licenses)),
      file = None
    )

  private def mapLicenseIds(ids: Seq[(String, Option[String])]): Seq[LicenseId] =
    ids.map {
      case (licenseName, licenseUrl) => LicenseId(licenseName, licenseUrl)
    }

  private def mapLicenses(licenses: Seq[LicenseId]): Seq[License] =
    licenses.map(mapLicense)

  private def mapLicense(licenseId: LicenseId): License =
    licenseId match {
      case LicenseId(licenseName, licenseUrl) =>
        val licenseId: Option[String] = licenseUrl
          .flatMap { url =>
            LicensesArchive.findByUrl(url)
          }
          .flatMap(_.id)
        License(licenseId, Some(licenseName), Seq())
    }
}
