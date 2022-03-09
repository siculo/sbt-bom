package sbtBom

import org.apache.commons.io.FileUtils
import org.cyclonedx.model.Bom
import org.cyclonedx.{BomGeneratorFactory, CycloneDxSchema}
import sbt.Keys.{sLog, target}
import sbt.{Compile, Def, File, Setting, _}
import sbtBom.BomSbtPlugin.autoImport._

import java.nio.charset.Charset

object BomSbtSettings {
  val schemaVersion: CycloneDxSchema.Version = CycloneDxSchema.Version.VERSION_10

  def makeBomTask(report: UpdateReport): Def.Initialize[Task[sbt.File]] = Def.task[File] {
    val log: Logger = sLog.value
    val bomFile = targetBomFile.value

    log.info(s"Creating bom file ${bomFile.getAbsolutePath}")

    val params = extractorParams
    val bom: Bom = new BomExtractor(params, report, log).bom
    val bomText: String = getXmlText(bom, schemaVersion)
    logBomInfo(log, params, bom)

    FileUtils.write(bomFile, bomText, Charset.forName("UTF-8"), false)

    log.info(s"Bom file ${bomFile.getAbsolutePath} created")

    bomFile
  }

  def listBomTask(report: UpdateReport): Def.Initialize[Task[String]] =
    Def.task[String] {
      val log: Logger = sLog.value

      log.info("Creating bom")

      val params = extractorParams
      val bom: Bom = new BomExtractor(params, report, log).bom
      val bomText: String = getXmlText(bom, schemaVersion)
      logBomInfo(log, params, bom)

      log.info("Bom created")

      bomText
    }

  private def extractorParams: BomExtractorParams =
    BomExtractorParams(schemaVersion, Compile)

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
