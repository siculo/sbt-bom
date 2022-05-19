package io.github.siculo.sbtbom

import sbt._

class MakeBomTask(taskSetup: TaskSetup,
                  bomFile: File)
  extends BomTask[File](taskSetup) {

  override def execute: File = {
    log.info(s"Creating bom file ${bomFile.getAbsolutePath}")
    val bomText = getBomText
    writeToFile(bomFile, bomText)
    validateBomFile(bomFile)
    log.info(s"Bom file ${bomFile.getAbsolutePath} created")
    bomFile
  }
}

