package sbtBom

import sbt.Keys._
import sbt._

import scala.xml.XML

object BomSbtPlugin extends AutoPlugin {

  override def trigger: PluginTrigger = allRequirements

  override val requires: Plugins = plugins.JvmPlugin

  object autoImport extends BomSbtKeys

  import autoImport._

  override lazy val projectSettings: Seq[Setting[_]] = {
    Seq(
      targetBomFile := target.value / "bom.xml",
      makeBom := makeBomTask.value
    )
  }

  private def makeBomTask = Def.task[File] {
    val log = sLog.value
    val bomFile = targetBomFile.value

    log.info(s"Creating bom file ${bomFile.getAbsolutePath}")

    val bomContent = new BomBuilder().build
    bomFile.getParentFile().mkdirs()
    XML.save(bomFile.getAbsolutePath, bomContent, xmlDecl = true)

    log.info(s"Bom file ${bomFile.getAbsolutePath} created")

    bomFile
  }

}
