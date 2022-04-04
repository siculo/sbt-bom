package io.github.siculo.sbtbom

import _root_.io.github.siculo.sbtbom.BomSbtPlugin.autoImport._
import org.apache.commons.io.FileUtils
import org.cyclonedx.model.Bom
import org.cyclonedx.{BomGeneratorFactory, CycloneDxSchema}
import sbt.Keys.{configuration, sLog, target}
import sbt.{Compile, Configuration, Def, File, IntegrationTest, Provided, Runtime, Test, _}

import java.nio.charset.Charset

object BomSbtSettings {
  val schemaVersion: CycloneDxSchema.Version = CycloneDxSchema.Version.VERSION_10

  def makeBomTask(report: UpdateReport, currentConfiguration: Configuration): Def.Initialize[Task[sbt.File]] = Def.task[File] {
    val log: Logger = sLog.value

    val bomFile = target.value / (currentConfiguration / bomFileName).value

    log.info(s"Creating bom file ${bomFile.getAbsolutePath}")

    val params = extractorParams(currentConfiguration)
    val bom: Bom = new BomExtractor(params, report, log).bom
    val bomText: String = getXmlText(bom, schemaVersion)
    logBomInfo(log, params, bom)

    FileUtils.write(bomFile, bomText, Charset.forName("UTF-8"), false)

    log.info(s"Bom file ${bomFile.getAbsolutePath} created")

    bomFile
  }

  def listBomTask(report: UpdateReport, currentConfiguration: Configuration): Def.Initialize[Task[String]] =
    Def.task[String] {
      val log: Logger = sLog.value

      log.info("Creating bom")

      val params = extractorParams(currentConfiguration)
      val bom: Bom = new BomExtractor(params, report, log).bom
      val bomText: String = getXmlText(bom, schemaVersion)
      logBomInfo(log, params, bom)

      log.info("Bom created")

      bomText
    }

  def bomConfigurationTask(currentConfiguration: Option[Configuration]): Def.Initialize[Task[Seq[Configuration]]] =
    Def.task[Seq[Configuration]] {
      val log: Logger = sLog.value
      val usedConfiguration: Configuration = currentConfiguration match {
        case Some(c) =>
          log.info(s"Using configuration ${c.name}")
          c
        case None =>
          log.info(s"Using default configuration ${Compile.name}")
          Compile
      }
      usedConfiguration match {
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

  private def extractorParams(currentConfiguration: Configuration): BomExtractorParams =
    BomExtractorParams(schemaVersion, currentConfiguration)

  private def logBomInfo(log: Logger, params: BomExtractorParams, bom: Bom): Unit = {
    log.info(s"Schema version: ${schemaVersion.getVersionString}")
    log.info(s"Serial number : ${bom.getSerialNumber}")
    log.info(s"Scope         : ${params.configuration.id}")
  }

  private def getXmlText(bom: Bom, schemaVersion: CycloneDxSchema.Version) = {
    val bomGenerator = BomGeneratorFactory.createXml(schemaVersion, bom)
    bomGenerator.generate
    val bomText = bomGenerator.toXmlString
    bomText
  }

}
