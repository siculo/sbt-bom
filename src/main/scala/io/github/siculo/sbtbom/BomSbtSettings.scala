package io.github.siculo.sbtbom

import io.github.siculo.sbtbom.BomSbtPlugin.autoImport._
import sbt.Keys.{sLog, target}
import sbt._

object BomSbtSettings {
  def makeBomTask(report: UpdateReport, currentConfiguration: Configuration): Def.Initialize[Task[sbt.File]] = Def.task[File] {
    new MakeBomTask(
      TaskSetup(report, currentConfiguration, sLog.value, bomSchemaVersion.value),
      target.value / (currentConfiguration / bomFileName).value
    ).execute
  }

  def listBomTask(report: UpdateReport, currentConfiguration: Configuration): Def.Initialize[Task[String]] =
    Def.task[String] {
      new ListBomTask(TaskSetup(report, currentConfiguration, sLog.value, bomSchemaVersion.value)).execute
    }
}
