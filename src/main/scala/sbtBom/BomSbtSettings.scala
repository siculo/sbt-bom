package sbtBom

import com.github.packageurl.PackageURL
import org.apache.commons.io.FileUtils
import org.cyclonedx.model.{Bom, Component}
import org.cyclonedx.{BomGeneratorFactory, CycloneDxSchema}
import sbt.Keys.{sLog, target}
import sbt.librarymanagement.ModuleReport
import sbt.{Def, File, Setting, _}
import sbtBom.BomSbtPlugin.autoImport._

import java.nio.charset.Charset
import java.util
import java.util.UUID
import scala.collection.JavaConverters._

object BomSbtSettings {
  def projectSettings: Seq[Setting[_]] = {
    // val configs = Seq(Compile, Test, IntegrationTest, Runtime, Provided, Optional)
    Seq(
      targetBomFile := target.value / "bom.xml",
      makeBom := Def.taskDyn(makeBomTask(Classpaths.updateTask.value)).value,
      listBom := Def.taskDyn(listBomTask(Classpaths.updateTask.value)).value,
    )
  }

  private def makeBomTask(report: UpdateReport): Def.Initialize[Task[sbt.File]] = Def.task[File] {
    val log = sLog.value
    val bomFile = targetBomFile.value

    log.info(s"Creating bom file ${bomFile.getAbsolutePath}")

    val schemaVersion: CycloneDxSchema.Version = CycloneDxSchema.Version.VERSION_10
    val bom: Bom = getBomFromUpdateReport(report)
    val bomText: String = getXmlText(bom, schemaVersion)
    FileUtils.write(bomFile, bomText, Charset.forName("UTF-8"), false)

    log.info(s"Schema version: ${schemaVersion.getVersionString}")
    log.info(s"Serial number : ${bom.getSerialNumber}")

    log.info(s"Bom file ${bomFile.getAbsolutePath} created")

    bomFile
  }

  private def listBomTask(report: UpdateReport): Def.Initialize[Task[String]] =
    Def.task[String] {
      val log = sLog.value

      log.info("Creating bom")

      val schemaVersion: CycloneDxSchema.Version = CycloneDxSchema.Version.VERSION_10
      val bom: Bom = getBomFromUpdateReport(report)
      val bomText: String = getXmlText(bom, schemaVersion)

      log.info(s"Schema version: ${schemaVersion.getVersionString}")
      log.info(s"Serial number : ${bom.getSerialNumber}")

      log.info("Bom created")

      bomText
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

  private def logComponent(log: Logger, component: Component): Unit = {
    log.info(s"Group ID      : ${component.getGroup}")
    log.info(s"Artifact name : ${component.getName}")
    log.info(s"Version       : ${component.getVersion}")
    log.info(s"Modified      : ${component.getModified}")
    log.info(s"Component type: ${component.getType.getTypeName}")
    log.info(s"Scope         : ${component.getScope.getScopeName}")
  }

  private def getXmlText(bom: Bom, schemaVersion: CycloneDxSchema.Version) = {
    val bomGenerator = BomGeneratorFactory.createXml(schemaVersion, bom)
    bomGenerator.generate
    val bomText = bomGenerator.toXmlString
    bomText
  }

  private def getBomFromUpdateReport(report: UpdateReport): Bom = {
    val serialNumber: String = "urn:uuid:" + UUID.randomUUID.toString
    val components: Seq[Component] = getComponentsFromReport(report)
    val bom = new Bom
    bom.setSerialNumber(serialNumber)
    bom.setComponents(components.asJava)
    bom
  }

  private def getComponentsFromReport(report: UpdateReport): Seq[Component] = {
    (report.configuration(Compile) map {
      configurationReport =>
        configurationReport.modules.map {
          module =>
            val component = componentFromModuleReport(module)
            component
        }
    }).getOrElse(Seq())
  }
}
