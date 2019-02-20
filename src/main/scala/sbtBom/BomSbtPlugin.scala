package sbtBom

import sbt.Keys._
import sbt._

object BomSbtPlugin extends AutoPlugin {

  override def trigger: PluginTrigger = allRequirements

  object autoImport extends BomSbtKeys

  import autoImport._

  override lazy val projectSettings: Seq[Setting[_]] = Seq(
    targetBomFile := target.value / "bom.xml",
    makeBom := makeBomTask.value
  )

  private def makeBomTask = Def.task[File] {
    val log = sLog.value

    lazy val bom = targetBomFile.value

    log.info("Creating bom file...")

    bom
  }

}
