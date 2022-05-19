package io.github.siculo.sbtbom.extractor

import com.github.packageurl.PackageURL
import io.github.siculo.sbtbom.ReportModel._
import org.cyclonedx.CycloneDxSchema
import org.cyclonedx.model.{Component, License, LicenseChoice}

import java.util

class LibraryComponentCreator(setup: BomCreatorSetup, dependency: Dependency) {
  val componentType = Component.Type.LIBRARY
  /*
    todo evaluate
        <publisher>The person(s) or organization(s) that published the component</publisher> [evaluate]
    done
        <group>org.scala-lang</group>
        <name>scala-library (!)</name>
        <version>2.13.8 (!)</version>
    todo may be ok
        <scope>required</scope>
    todo
        <hashes>
          <hash>bom:hashType</hash>
        </hashes>
    todo evaluate license id
        <licenses>
          <license>
            <id>?</id>
            <name>Apache-2.0</name>
          </license>
        </licenses>
    todo evaluate
        <copyright>optional copyright</copyright> [evaluate]
        <cpe>@https://nvd.nist.gov/products/cpe</cpe> [evaluate]
    done
        <purl>pkg:maven/org.scala-lang/scala-library@2.13.8</purl>
    todo evaluate
        <modified>false!(derivative of the original or not)</modified>
    todo evaluate subcomponent
        <components>
          <component>sub-component</component>
        </components>
  */

  def create: Component = {
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
    if (dependency.licenses.nonEmpty) {
      val choice = new LicenseChoice()
      dependency.licenses.foreach {
        modelLicense =>
          val license = new License()
          license.setName(modelLicense.name)
          if (setup.schemaVersion != CycloneDxSchema.Version.VERSION_10) {
            modelLicense.url.foreach(license.setUrl)
          }
          choice.addLicense(license)
      }
      component.setLicenseChoice(choice)
    }
    component
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
