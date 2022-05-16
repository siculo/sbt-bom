package io.github.siculo.sbtbom.extractor

import sbt._
import org.cyclonedx.CycloneDxSchema

case class ExtractorContext(schemaVersion: CycloneDxSchema.Version,
                            configuration: Configuration,
                            log: Logger)
