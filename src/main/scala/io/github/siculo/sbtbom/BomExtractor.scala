package io.github.siculo.sbtbom

import com.github.packageurl.PackageURL
import org.cyclonedx.model.{Bom, Component, License, LicenseChoice}
import sbt.librarymanagement.ModuleReport
import sbt._

import java.util
import java.util.UUID
import scala.collection.JavaConverters._

class BomExtractor(settings: BomExtractorParams, report: UpdateReport, log: Logger) {
  private val serialNumber: String = "urn:uuid:" + UUID.randomUUID.toString

  def bom: Bom = {
    val bom = new Bom
    bom.setSerialNumber(serialNumber)
    bom.setComponents(components.asJava)
    bom
  }

  private def components: Seq[Component] =
    configurationsForComponents(settings.configuration).foldLeft(Seq[Component]()) {
      case (collected, configuration) =>
        collected ++ componentsForConfiguration(configuration)
    }

  private def configurationsForComponents(configuration: Configuration): Seq[sbt.Configuration] = {
    log.info(s"Current configuration = ${configuration.name}")
    configuration match {
      case Test =>
        Seq(Test, Runtime, Compile)
      case IntegrationTest =>
        Seq(IntegrationTest, Runtime, Compile)
      case Runtime =>
        Seq(Runtime, Compile)
      case Compile =>
        Seq(Compile)
      case Provided =>
        Seq(Provided)
      case anyOtherConfiguration: Configuration =>
        Seq(anyOtherConfiguration)
      case _ =>
        Seq()
    }
  }

  private def componentsForConfiguration(configuration: Configuration): Seq[Component] = {
    (report.configuration(configuration) map {
      configurationReport =>
        log.info(s"Configuration name = ${configurationReport.configuration.name}, modules: ${configurationReport.modules.size}")
        configurationReport.modules.map {
          module =>
            new ComponentExtractor(module).component
        }
    }).getOrElse(Seq())
  }

  class ComponentExtractor(moduleReport: ModuleReport) {
    def component: Component = {
      val group = moduleReport.module.organization
      val name = moduleReport.module.name
      val version = moduleReport.module.revision

      val component = new Component()
      component.setGroup(group)
      component.setName(name)
      component.setVersion(moduleReport.module.revision)
      component.setModified(false)
      component.setType(Component.Type.LIBRARY)
      component.setPurl(
        new PackageURL(PackageURL.StandardTypes.MAVEN, group, name, version, new util.TreeMap(), null).canonicalize()
      )
      component.setScope(Component.Scope.REQUIRED)
      licenseChoice.foreach(component.setLicenseChoice)

      // logComponent(component)

      component
    }

    private def licenseChoice: Option[LicenseChoice] = {
      if (moduleReport.licenses.isEmpty)
        None
      else {
        val choice = new LicenseChoice()
        moduleReport.licenses.foreach {
          case (name, mayBeUrl) =>
            val license = new License()
            license.setName(name)
            mayBeUrl.foreach(license.setUrl)
            choice.addLicense(license)
        }
        Some(choice)
      }
    }
  }

  private def logComponent(component: Component): Unit = {
    log.info(
      s""""${component.getGroup}" % "${component.getName}" % "${component.getVersion}", Modified = ${component.getModified}, Component type = ${component.getType.getTypeName}, Scope = ${component.getScope.getScopeName}""".stripMargin)
  }

}
