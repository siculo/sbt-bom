package sbtBom

import sbt.librarymanagement.{ConfigurationReport, ModuleReport}
import sbtBom.model.{Dependencies, Dependency, License}

class Settings2Dependencies {
  private val unlicensed = Seq(License(id = Some("Unlicense")))

  def create(reportOption: Option[ConfigurationReport]): Dependencies = {
    val dependencies = reportOption.map(createDependencies).getOrElse(Seq[Dependency]())
    Dependencies(dependencies)
  }

  private def createDependencies(configurationReport: ConfigurationReport): Seq[Dependency] = {
    configurationReport.modules.map(createDependency)
  }

  private def createDependency(moduleReport: ModuleReport): Dependency = {
    Dependency(
      moduleReport.module.organization,
      moduleReport.module.name,
      moduleReport.module.revision,
      modified = false,
      licenses = createLicenses(moduleReport.licenses),
      file = None
    )
  }

  private def createLicenses(licenses: Seq[(String, Option[String])]): Seq[License] = {
    if (licenses.isEmpty)
      unlicensed
    else
      licenses.map(createLicense)
  }

  private def createLicense(license: (String, Option[String])): License = {
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
