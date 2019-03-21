package sbtBom

import java.io.FileOutputStream
import java.nio.channels.Channels

import sbt.{Def, File, Setting, _}
import sbt.Keys.{sLog, target}
import sbtBom.BomSbtPlugin.autoImport._

import scala.xml.{Elem, PrettyPrinter, XML}
import scala.util.control.Exception.ultimately

object BomSbtSettings {
  def projectSettings: Seq[Setting[_]] = {
    // val configs = Seq(Compile, Test, IntegrationTest, Runtime, Provided, Optional)
    Seq(
      targetBomFile := target.value / "bom.xml",
      makeBom := Def.taskDyn(makeBomTask(Classpaths.updateTask.value)).value,
      listBom := Def.taskDyn(listBomTask(Classpaths.updateTask.value)).value,
    )
  }

  private def makeBomTask(
      report: UpdateReport): Def.Initialize[Task[sbt.File]] = Def.task[File] {
    val log = sLog.value
    val bomFile = targetBomFile.value

    log.info(s"Creating bom file ${bomFile.getAbsolutePath}")

    writeXmlToFile(new BomBuilder(report.configuration(Compile)).build,
                   "UTF-8",
                   bomFile)

    log.info(s"Bom file ${bomFile.getAbsolutePath} created")

    bomFile
  }

  private def listBomTask(report: UpdateReport): Def.Initialize[Task[String]] =
    Def.task[String] {
      val log = sLog.value

      log.info("Creating bom")

      val bomText =
        xmlToText(new BomBuilder(report.configuration(Compile)).build, "UTF8")

      log.info("Bom created")

      bomText
    }

  private def writeXmlToFile(xml: Elem,
                             encoding: String,
                             destFile: sbt.File): Unit =
    writeToFile(xmlToText(xml, encoding), encoding, destFile)

  private def xmlToText(bomContent: Elem, encoding: String): String =
    "<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>\n" +
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
