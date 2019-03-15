package sbtBom

import java.io.FileOutputStream
import java.nio.channels.Channels

import sbt.{Def, File, Setting, _}
import sbt.Keys.{sLog, target}
import sbtBom.BomSbtPlugin.autoImport.{listAll, makeBom, targetBomFile}

import scala.xml.{Elem, PrettyPrinter, XML}
import scala.util.control.Exception.ultimately

object BomSbtSettings {
  def projectSettings: Seq[Setting[_]] = {
    val configs = Seq(Compile, Test, IntegrationTest, Runtime, Provided, Optional)
    Seq(
      targetBomFile := target.value / "bom.xml",
      makeBom := makeBomTask.value,
    ) ++ configs.map(listAll := printReport(Classpaths.updateTask.value, _))
  }

  private def printReport(report: UpdateReport, config: Configuration): Unit = {
    report.configuration(config).map {
      r =>
        println("listing all dependencies")
        println(r.toString())
        println(s"configuration: ${config.name}")
    }
  }

  private def listAllTask: Def.Initialize[Task[Unit]] = Def.task[Unit] {
    val log = sLog.value

    log.info("listing all dependencies")
  }

  private def makeBomTask: Def.Initialize[Task[sbt.File]] = Def.task[File] {
    val log = sLog.value
    val bomFile = targetBomFile.value

    log.info(s"Creating bom file ${bomFile.getAbsolutePath}")

    writeXmlToFile(new BomBuilder().build, "UTF8", bomFile)

    log.info(s"Bom file ${bomFile.getAbsolutePath} created")

    bomFile
  }

  private def writeXmlToFile(xml: Elem,
                             encoding: String,
                             destFile: sbt.File): Unit =
    writeToFile(xmlToText(xml, encoding), encoding, destFile)

  private def xmlToText(bomContent: Elem, encoding: String): String =
    "<?xml version='1.0' encoding='" + encoding + "'?>\n" +
      new PrettyPrinter(80, 2).format(bomContent)

  private def writeToFile(content: String,
                          encoding: String,
                          destFile: sbt.File): Unit = {
    destFile.getParentFile().mkdirs()
    val fos = new FileOutputStream(destFile.getAbsolutePath)
    val writer = Channels.newWriter(fos.getChannel(), encoding)
    ultimately(writer.close())(
      writer.write(content)
    )
  }
}
