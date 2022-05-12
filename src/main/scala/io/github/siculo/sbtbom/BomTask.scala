package io.github.siculo.sbtbom

import org.apache.commons.io.FileUtils
import org.cyclonedx.model.Bom
import org.cyclonedx.parsers.XmlParser
import org.cyclonedx.{BomGeneratorFactory, CycloneDxSchema}
import sbt._

import java.nio.charset.Charset

case class BomTaskProperties(report: UpdateReport, currentConfiguration: Configuration, log: Logger, schemaVersion: CycloneDxSchema.Version)

abstract class BomTask[T](protected val properties: BomTaskProperties) {
  def execute: T

  protected def getBomText: String = {
    val params: BomExtractorParams = extractorParams(currentConfiguration)
    val bom: Bom = new BomExtractor(params, report, log).bom
    val bomText: String = getXmlText(bom)
    logBomInfo(params, bom)
    bomText
  }

  protected def writeToFile(destFile: File, text: String): Unit = {
    FileUtils.write(destFile, text, Charset.forName("UTF-8"), false)
  }

  protected def validateBomFile(bomFile: File): Unit = {
    val parser = new XmlParser()
    if (!parser.isValid(bomFile, schemaVersion)) {
      raiseException(s"The BOM file ${bomFile.getAbsolutePath} does not conform to the CycloneDX BOM standard as defined by the XSD")
    }
  }

  protected def raiseException(message: String): Unit = {
    log.error(message)
    throw new BomError(message)
  }

  private def extractorParams(currentConfiguration: Configuration): BomExtractorParams =
    BomExtractorParams(schemaVersion, currentConfiguration)

  private def getXmlText(bom: Bom): String = {
    val bomGenerator = BomGeneratorFactory.createXml(schemaVersion, bom)
    bomGenerator.generate
    val bomText = bomGenerator.toXmlString
    bomText
  }

  protected def logBomInfo(params: BomExtractorParams, bom: Bom): Unit = {
    log.info(s"Schema version: ${schemaVersion.getVersionString}")
    log.info(s"Serial number : ${bom.getSerialNumber}")
    log.info(s"Scope         : ${params.configuration.id}")
  }

  protected def report: UpdateReport = properties.report

  protected def currentConfiguration: Configuration = properties.currentConfiguration

  protected def log: Logger = properties.log

  protected def schemaVersion: CycloneDxSchema.Version = properties.schemaVersion
}
