package io.github.siculo.sbtbom.creator

import io.github.siculo.sbtbom.ReportModel._
import org.cyclonedx.CycloneDxSchema
import org.cyclonedx.model.{Bom, Component}

import java.util.UUID
import scala.collection.JavaConverters._

class BomCreator(creatorSetup: BomCreatorSetup) {
  private val serialNumber: String = "urn:uuid:" + UUID.randomUUID.toString
  private val libraryComponentCreator = new LibraryComponentCreator(creatorSetup)

  def create(dependencyReport: DependencyReport): Bom = {
    val bom = new Bom
    if (creatorSetup.schemaVersion != CycloneDxSchema.Version.VERSION_10) {
      bom.setSerialNumber(serialNumber)
    }
    bom.setComponents(components(dependencyReport.dependencies).asJava)
    bom
  }

  private def components(dependencies: Seq[Dependency]): Seq[Component] = {
    creatorSetup.log.info(s"Current configuration = ${creatorSetup.configuration.name}")
    dependencies.map {
      dependency =>
        libraryComponentCreator.create(dependency)
    }
  }

}
