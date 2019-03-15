package sbtBom

import sbt._

trait BomSbtKeys {

  lazy val targetBomFile = settingKey[File]("target file to store the generated bom")

  lazy val makeBom = taskKey[File]("Generates bom file which includes all project dependencies")

  lazy val listAll = taskKey[Unit]("List all dependencies")

}
