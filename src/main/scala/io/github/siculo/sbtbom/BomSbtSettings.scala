package io.github.siculo.sbtbom

import io.github.siculo.sbtbom.BomSbtPlugin.autoImport._
import sbt.Keys.{sLog, target}
import sbt._

object BomSbtSettings {
  def makeBomTask(report: UpdateReport, currentConfiguration: Configuration): Def.Initialize[Task[sbt.File]] = Def.task[File] {
    val reportFile = dependencyReportFileName.value.map(target.value / _)
    new MakeBomTask(
      TaskSetup(report, currentConfiguration, bomSchemaVersion.value, reportFile, sLog.value),
      target.value / (currentConfiguration / bomFileName).value
    ).execute
  }

  def listBomTask(report: UpdateReport, currentConfiguration: Configuration): Def.Initialize[Task[String]] =
    Def.task[String] {
      val reportFile = dependencyReportFileName.value.map(target.value / _)
      new ListBomTask(
        TaskSetup(report, currentConfiguration, bomSchemaVersion.value, reportFile, sLog.value)
      ).execute
    }
}
