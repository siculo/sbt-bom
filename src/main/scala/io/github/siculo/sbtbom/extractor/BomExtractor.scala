package io.github.siculo.sbtbom.extractor

import io.github.siculo.sbtbom.ReportModel._
import org.cyclonedx.CycloneDxSchema
import org.cyclonedx.model.{Bom, Component}

import java.util.UUID
import scala.collection.JavaConverters._

class BomExtractor(setup: ExtractorSetup, dependencyReport: DependencyReport) {

  private val serialNumber: String = "urn:uuid:" + UUID.randomUUID.toString

  def extract: Bom = {
    val bom = new Bom
    if (setup.schemaVersion != CycloneDxSchema.Version.VERSION_10) {
      bom.setSerialNumber(serialNumber)
    }
    bom.setComponents(components().asJava)
    bom
  }

  private def components(): Seq[Component] = {
    setup.log.info(s"Current configuration = ${setup.configuration.name}")
    dependencyReport.dependencies.map {
      dependency =>
        new LibraryComponentExtractor(setup, dependency).extract
    }
  }

}
