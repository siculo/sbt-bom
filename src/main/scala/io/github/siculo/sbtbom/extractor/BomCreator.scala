package io.github.siculo.sbtbom.extractor

import io.github.siculo.sbtbom.ReportModel._
import org.cyclonedx.CycloneDxSchema
import org.cyclonedx.model.{Bom, Component}

import java.util.UUID
import scala.collection.JavaConverters._

class BomCreator(creatorSetup: BomCreatorSetup, dependencyReport: DependencyReport) {

  private val serialNumber: String = "urn:uuid:" + UUID.randomUUID.toString

  def create: Bom = {
    val bom = new Bom
    if (creatorSetup.schemaVersion != CycloneDxSchema.Version.VERSION_10) {
      bom.setSerialNumber(serialNumber)
    }
    bom.setComponents(components().asJava)
    bom
  }

  private def components(): Seq[Component] = {
    creatorSetup.log.info(s"Current configuration = ${creatorSetup.configuration.name}")
    dependencyReport.dependencies.map {
      dependency =>
        new LibraryComponentCreator(creatorSetup, dependency).create
    }
  }

}
