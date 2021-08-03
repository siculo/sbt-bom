package sbtBom

import sbt.{Configuration, UpdateReport}
import sbt.librarymanagement.{ConfigurationReport, ModuleReport}
import sbtBom.model.{Dependencies, Dependency, License}

class UpdateReportInspector(report: UpdateReport) {
  private val unlicensed = Seq(License(id = Some("Unlicense")))

  def dependencies(configuration: Configuration): Dependencies = {
    val dependencies = report.configuration(configuration).map(mapDependencies).getOrElse(Seq[Dependency]())
    Dependencies(dependencies)
  }

  private def mapDependencies(configurationReport: ConfigurationReport): Seq[Dependency] = {
    configurationReport.modules.map(mapDependency)
  }

  private def mapDependency(moduleReport: ModuleReport): Dependency = {
    Dependency(
      moduleReport.module.organization,
      moduleReport.module.name,
      moduleReport.module.revision,
      modified = false,
      licenses = mapLicenses(moduleReport.licenses),
      file = None
    )
  }

  private def mapLicenses(licenses: Seq[(String, Option[String])]): Seq[License] = {
    if (licenses.isEmpty)
      unlicensed
    else
      licenses.map(mapLicense)
  }

  private def mapLicense(license: (String, Option[String])): License = {
    license match {
      case (licenseName, licenseUrl) =>
        val licenseId: Option[String] = licenseUrl
          .flatMap { url =>
            LicensesArchive.findByUrl(url)
          }
          .flatMap(_.id)
        License(licenseId, Some(licenseName), Seq())
    }
  }
}
