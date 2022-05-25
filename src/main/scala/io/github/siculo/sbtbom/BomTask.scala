package io.github.siculo.sbtbom

import io.github.siculo.sbtbom.ReportModelExtraction._
import io.github.siculo.sbtbom.PluginConstants._
import io.github.siculo.sbtbom.ReportModel._
import io.github.siculo.sbtbom.creator._
import org.apache.commons.io.FileUtils
import org.cyclonedx.model.Bom
import org.cyclonedx.parsers.XmlParser
import org.cyclonedx.{BomGeneratorFactory, CycloneDxSchema}
import sbt._
import play.api.libs.json._
import java.nio.charset.Charset
import scala.collection.JavaConverters._

case class TaskSetup(report: sbt.UpdateReport,
                     currentConfiguration: sbt.Configuration,
                     schemaVersion: String,
                     reportFile: Option[File],
                     log: sbt.Logger)

abstract class BomTask[T](protected val taskSetup: TaskSetup) {

  def execute: T

  protected def getBomText: String = {
    val creatorSetup: BomCreatorSetup = BomCreatorSetup(schemaVersion, currentConfiguration, log)
    val dependencyReport: DependencyReport = report.asDependencyReportForConfiguration(currentConfiguration)
    val bom: Bom = new BomCreator(creatorSetup, dependencyReport).create
    logBomInfo(creatorSetup, bom)
    taskSetup.reportFile.foreach {
      file =>
        log.info(s"Creating report file ${file.getPath}")
        writeToFile(file, Json.prettyPrint(Json.toJson(dependencyReport)))
    }
    getXmlText(bom)
  }

  protected def writeToFile(destFile: File, text: String): Unit = {
    FileUtils.write(destFile, text, Charset.forName("UTF-8"), false)
  }

  protected def validateBomFile(bomFile: File): Unit = {
    val cyclonedxParser = new XmlParser()
    val exceptions = cyclonedxParser.validate(bomFile, schemaVersion).asScala
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
    val cyclonedxBomGenerator = BomGeneratorFactory.createXml(schemaVersion, bom)
    cyclonedxBomGenerator.generate
    cyclonedxBomGenerator.toXmlString
  }

  protected def logBomInfo(creatorSetup: BomCreatorSetup, bom: Bom): Unit = {
    log.info(s"Schema version: ${schemaVersion.getVersionString}")
    if (creatorSetup.schemaVersion != CycloneDxSchema.Version.VERSION_10) {
      log.info(s"Serial number : ${bom.getSerialNumber}")
    }
    log.info(s"Scope         : ${creatorSetup.configuration.id}")
  }

  protected val report: UpdateReport = taskSetup.report

  protected val currentConfiguration: Configuration = taskSetup.currentConfiguration

  protected val log: Logger = taskSetup.log

  protected lazy val schemaVersion: CycloneDxSchema.Version =
    supportedVersions.find(_.getVersionString == taskSetup.schemaVersion) match {
      case Some(foundVersion) => foundVersion
      case None =>
        val message = s"Unsupported schema version ${taskSetup.schemaVersion}"
        log.error(message)
        throw new TaskError(message)
    }
}
