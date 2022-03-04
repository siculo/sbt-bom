package sbtBom

import com.github.packageurl.PackageURL
import org.cyclonedx.model.{Bom, Component}
import sbt.librarymanagement.ModuleReport
import sbt.{Logger, UpdateReport, _}

import java.util
import java.util.UUID
import scala.collection.JavaConverters._


class BomExtractor(report: UpdateReport, log: Logger) {
  def bom: Bom = {
    val serialNumber: String = "urn:uuid:" + UUID.randomUUID.toString
    val bom = new Bom
    bom.setSerialNumber(serialNumber)
    bom.setComponents(components.asJava)
    bom
  }

  private def components: Seq[Component] = {
    (report.configuration(Compile) map {
      configurationReport =>
        configurationReport.modules.map {
          module =>
            val component = componentFromModuleReport(module)
            component
        }
    }).getOrElse(Seq())
  }

  private def componentFromModuleReport(moduleReport: ModuleReport): Component = {
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
    component
  }

  private def logComponent(component: Component): Unit = {
    log.info(s"Group ID      : ${component.getGroup}")
    log.info(s"Artifact name : ${component.getName}")
    log.info(s"Version       : ${component.getVersion}")
    log.info(s"Modified      : ${component.getModified}")
    log.info(s"Component type: ${component.getType.getTypeName}")
    log.info(s"Scope         : ${component.getScope.getScopeName}")
  }
}
