package io.github.siculo.sbtbom.creator

import com.github.packageurl.PackageURL
import io.github.siculo.sbtbom.ReportModel
import io.github.siculo.sbtbom.ReportModel._
import org.cyclonedx.model.{Component, Hash, LicenseChoice}
import org.cyclonedx.util.BomUtils

import java.io.File
import java.util
import scala.collection.JavaConverters._

class LibraryComponentCreator(setup: BomCreatorSetup) {
  private val licenseCreator = new LicenseCreator(setup)

  /*
    todo evaluate
        <publisher>The person(s) or organization(s) that published the component</publisher> [evaluate]
    todo may be ok
        <scope>required</scope>
    todo evaluate
        <copyright>optional copyright</copyright> [evaluate]
        <cpe>@https://nvd.nist.gov/products/cpe</cpe> [evaluate]
    todo evaluate
        <modified>false!(derivative of the original or not)</modified>
    todo evaluate subcomponent
        <components>
          <component>sub-component</component>
        </components>
  */

  def create(dependency: Dependency): Component = {
    val component = new Component()
    component.setGroup(dependency.group)
    component.setName(dependency.name)
    component.setVersion(dependency.version)
    component.setModified(dependency.modified)
    component.setType(Component.Type.LIBRARY)
    component.setPurl(
      new PackageURL(PackageURL.StandardTypes.MAVEN, dependency.group, dependency.name, dependency.version, new util.TreeMap(), null).canonicalize()
    )
    component.setScope(Component.Scope.REQUIRED)
    component.setHashes(hashes(dependency.filePaths).asJava)
    if (dependency.licenses.nonEmpty) {
      component.setLicenseChoice(createLicenseChoice(dependency.licenses))
    }
    component
  }

  private def createLicenseChoice(licenses: Seq[ReportModel.License]): LicenseChoice = {
    val licenseChoice = new LicenseChoice()
    licenses.foreach {
      modelLicense =>
        licenseChoice.addLicense(licenseCreator.create(modelLicense))
    }
    licenseChoice
  }

  private def hashes(filePaths: Seq[String]): Seq[Hash] = {
    filePaths.flatMap {
      filePath =>
        BomUtils.calculateHashes(new File(filePath), setup.schemaVersion).asScala
    }
  }

  private def logComponent(component: Component): Unit = {
    setup.log.info(
      s""""
         |${component.getGroup}" % "${component.getName}" % "${component.getVersion}",
         | Modified = ${component.getModified}, Component type = ${component.getType.getTypeName},
         | Scope = ${component.getScope.getScopeName}
         | """.stripMargin)
  }
}
