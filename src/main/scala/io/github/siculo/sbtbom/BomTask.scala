package io.github.siculo.sbtbom

import io.github.siculo.sbtbom.PluginConstants._
import io.github.siculo.sbtbom.ReportModel._
import io.github.siculo.sbtbom.extractor._
import org.apache.commons.io.FileUtils
import org.cyclonedx.model.Bom
import org.cyclonedx.parsers.XmlParser
import org.cyclonedx.{BomGeneratorFactory, CycloneDxSchema}
import sbt._
import _root_.io.github.siculo.sbtbom.ReportFacade._

import java.nio.charset.Charset
import scala.collection.JavaConverters._

case class TaskParams(report: UpdateReport, currentConfiguration: Configuration, log: Logger, schemaVersion: String)

abstract class BomTask[T](protected val params: TaskParams) {

  def execute: T

  protected def getBomText: String = {
    val setup: ExtractorSetup = ExtractorSetup(schemaVersion, currentConfiguration, log)
    val dependencyReport: DependencyReport = report.dependencyReport(currentConfiguration)
    val bom: Bom = new BomExtractor(setup, dependencyReport).extract
    val bomText: String = getXmlText(bom)
    logBomInfo(setup, bom)
    bomText
  }

  protected def writeToFile(destFile: File, text: String): Unit = {
    FileUtils.write(destFile, text, Charset.forName("UTF-8"), false)
  }

  protected def validateBomFile(bomFile: File): Unit = {
    val parser = new XmlParser()
    val exceptions = parser.validate(bomFile, schemaVersion).asScala
    if (exceptions.nonEmpty) {
      val message = s"The BOM file ${bomFile.getAbsolutePath} does not conform to the CycloneDX BOM standard as defined by the XSD"
      log.error(s"$message:")
      exceptions.foreach {
        exception =>
          log.error(s"- ${exception.getMessage}")
      }
      throw new TaskError(message)
    }
  }

  @throws[TaskError]
  protected def raiseException(message: String): Unit = {
    log.error(message)
    throw new TaskError(message)
  }

  private def getXmlText(bom: Bom): String = {
    val bomGenerator = BomGeneratorFactory.createXml(schemaVersion, bom)
    bomGenerator.generate
    val bomText = bomGenerator.toXmlString
    bomText
  }

  protected def logBomInfo(setup: ExtractorSetup, bom: Bom): Unit = {
    log.info(s"Schema version: ${schemaVersion.getVersionString}")
    // log.info(s"Serial number : ${bom.getSerialNumber}")
    log.info(s"Scope         : ${setup.configuration.id}")
  }

  protected def report: UpdateReport = params.report

  protected def currentConfiguration: Configuration = params.currentConfiguration

  protected def log: Logger = params.log

  protected lazy val schemaVersion: CycloneDxSchema.Version =
    supportedVersions.find(_.getVersionString == params.schemaVersion) match {
      case Some(foundVersion) => foundVersion
      case None =>
        val message = s"Unsupported schema version ${params.schemaVersion}"
        log.error(message)
        throw new TaskError(message)
    }
}
