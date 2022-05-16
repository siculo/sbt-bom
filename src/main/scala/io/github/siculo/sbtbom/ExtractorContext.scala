package io.github.siculo.sbtbom

import org.cyclonedx.CycloneDxSchema
import sbt.{Configuration, Logger}

case class ExtractorContext(schemaVersion: CycloneDxSchema.Version,
                            configuration: Configuration,
                            log: Logger)
