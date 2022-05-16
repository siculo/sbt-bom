package io.github.siculo.sbtbom

import org.cyclonedx.CycloneDxSchema
import org.cyclonedx.model.{Bom, Component}
import sbt._

import java.util.UUID
import scala.collection.JavaConverters._

class BomExtractor(context: ExtractorContext, source: UpdateReport) {
  import context._

  private val serialNumber: String = "urn:uuid:" + UUID.randomUUID.toString

  def extract: Bom = {
    val bom = new Bom
    if (schemaVersion != CycloneDxSchema.Version.VERSION_10) {
      bom.setSerialNumber(serialNumber)
    }
    bom.setComponents(components.asJava)
    bom
  }

  private def components: Seq[Component] =
    configurationsForComponents(configuration).foldLeft(Seq[Component]()) {
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
    (source.configuration(configuration) map {
      configurationReport =>
        log.info(s"Configuration name = ${configurationReport.configuration.name}, modules: ${configurationReport.modules.size}")
        configurationReport.modules.map {
          module =>
            new ComponentExtractor(context, module).extract
        }
    }).getOrElse(Seq())
  }
}
