package sbtBom

import org.cyclonedx.CycloneDxSchema
import sbt.Configuration

case class BomExtractorParams(schemaVersion: CycloneDxSchema.Version,
                              configuration: Configuration)
