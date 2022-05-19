package io.github.siculo.sbtbom.creator

import sbt._
import org.cyclonedx.CycloneDxSchema

case class BomCreatorSetup(schemaVersion: CycloneDxSchema.Version,
                           configuration: Configuration,
                           log: Logger)
