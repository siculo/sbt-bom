package io.github.siculo.sbtbom

import sbt.Keys.{artifact, target, version}
import sbt._

/**
 * plugin object
 */
object BomSbtPlugin extends AutoPlugin {

  override def requires: Plugins = empty

  override def trigger: PluginTrigger = allRequirements

  object autoImport {
    lazy val targetBomFile: SettingKey[sbt.File] = settingKey[File]("target file to store the generated bom")
    lazy val makeBom: TaskKey[sbt.File] = taskKey[sbt.File]("Generates bom file which includes all project dependencies")
    lazy val listBom: TaskKey[String] = taskKey[String]("Returns a bom which includes all project dependencies")
    lazy val supportedConfigurations: Seq[Configuration] = Seq(Compile, Runtime, Test)
  }

  import autoImport._

  override lazy val projectSettings: Seq[Setting[_]] = {
    val bomFileName = Def.setting {
      val artifactId = artifact.value.name
      val artifactVersion = version.value
      target.value / s"${artifactId}-${artifactVersion}.bom.xml"
    }
    Seq(
      targetBomFile := bomFileName.value,
      makeBom := Def.taskDyn(BomSbtSettings.makeBomTask(Classpaths.updateTask.value)).value,
      listBom := Def.taskDyn(BomSbtSettings.listBomTask(Classpaths.updateTask.value)).value,
    )
  }
}
