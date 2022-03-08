package sbtBom

import sbt._

/**
 * plugin keys
 */
trait BomSbtKeys {
  lazy val targetBomFile: SettingKey[sbt.File] = settingKey[File]("target file to store the generated bom")

  lazy val makeBom: TaskKey[sbt.File] = taskKey[sbt.File]("Generates bom file which includes all project dependencies")

  lazy val listBom: TaskKey[String] = taskKey[String]("Returns a bom which includes all project dependencies")

  lazy val supportedConfigurations: Seq[Configuration] = Seq(Compile, Runtime, Test)
}
