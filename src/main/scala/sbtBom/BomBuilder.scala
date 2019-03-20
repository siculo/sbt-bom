package sbtBom

import sbt.librarymanagement.{ConfigurationReport, ModuleReport}

import scala.xml.{Elem, NodeBuffer, XML}

class BomBuilder(reportOption: Option[ConfigurationReport]) {
  def build: Elem = {
    <bom xmlns="http://cyclonedx.org/schema/bom/1.0" version="1">
      <components>
        { reportOption.map(buildComponents(_)).getOrElse(Seq()) }
      </components>
    </bom>
  }

  private def buildComponents(report: ConfigurationReport): Seq[Elem] = {
    report.modules.map(buildModule(_))
  }

  private def buildModule(report: ModuleReport): Elem = {
    <component type="library">
      <group>{ report.module.organization }</group>
      <name>{ report.module.name }</name>
      <version>{ report.module.revision }</version>
      <licenses>{ buildLicenses(report.licenses) }</licenses>
      <modified>{ false }</modified>
    </component>
  }

  private def buildLicenses(licenses: Seq[(String, Option[String])]): Elem = {
    <licenses>
      {
        if (licenses.isEmpty) {
          unlicensed
        } else {
          licenses.map(buildLicense(_))
        }
      }
    </licenses>
  }

  private val unlicensed = {
    <license>
      <id>Unlicense</id>
    </license>
  }

  private def buildLicense(license: (String, Option[String])): Elem = {
    // todo: find the right id
    val licenseIdDescr = license._1.replace(' ', '-')
    val licenseId = license._2.flatMap {
      url =>
        LicensesArchive.findByUrl(url)
    }.map(_.id).getOrElse(licenseIdDescr)
    <license>
      <id>{licenseId}</id>
    </license>
  }
}
