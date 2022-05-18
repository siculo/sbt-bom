package io.github.siculo.sbtbom.extractor

import sbt._
import org.cyclonedx.CycloneDxSchema

case class ExtractorSetup(schemaVersion: CycloneDxSchema.Version,
                          configuration: Configuration,
                          log: Logger)
