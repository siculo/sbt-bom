package sbtBom

import sbt.librarymanagement.{ConfigurationReport, ModuleReport}

import scala.xml.{Elem, Node}

class OldBomBuilder(reportOption: Option[ConfigurationReport]) {
  def build: Elem = {
    <bom xmlns="http://cyclonedx.org/schema/bom/1.0" version="1">
      <components>
        {reportOption.map(buildComponents).getOrElse(Seq())}
      </components>
    </bom>
  }

  private def buildComponents(report: ConfigurationReport): Seq[Elem] = {
    report.modules.map(buildModule)
  }

  private def buildModule(report: ModuleReport): Elem = {
    <component type="library">
      <group>{report.module.organization}</group>
      <name>{report.module.name}</name>
      <version>{report.module.revision}</version>
      <licenses>{buildLicenses(report.licenses)}</licenses>
      <modified>{false}</modified>
    </component>
  }

  private def buildLicenses(licenses: Seq[(String, Option[String])]): Seq[Node] =
    if (licenses.isEmpty) {
      unlicensed
    } else {
      licenses.map(buildLicense)
    }

  private val unlicensed = {
    <license>
      <id>Unlicense</id>
    </license>
  }

  private def buildLicense(license: (String, Option[String])): Elem = {
    // todo: find the right id
    val licenseIdDescr = license._1.replace(' ', '-')
    val licenseId = license._2
      .flatMap { url =>
        LicensesArchive.findByUrl(url)
      }
      .map(_.id)
      .getOrElse(licenseIdDescr)
    <license>
      <name>{license._1}</name>
    </license>
  }
}
