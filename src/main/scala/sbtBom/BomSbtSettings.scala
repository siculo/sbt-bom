package sbtBom

import java.io.FileOutputStream
import java.nio.channels.Channels

import sbt.{Def, File, Setting, _}
import sbt.Keys.{sLog, target}
import sbtBom.BomSbtPlugin.autoImport.{makeBom, targetBomFile}

import scala.xml.{Elem, PrettyPrinter, XML}
import scala.util.control.Exception.ultimately

object BomSbtSettings {
  def projectSettings: Seq[Setting[_]] = {
    Seq(
      targetBomFile := target.value / "bom.xml",
      makeBom := makeBomTask.value
    )
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
