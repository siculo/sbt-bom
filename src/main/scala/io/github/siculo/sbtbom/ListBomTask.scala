package io.github.siculo.sbtbom

class ListBomTask(taskSetup: TaskSetup) extends BomTask[String](taskSetup) {
  override def execute: String = {
    log.info("Creating bom")
    val bomText = getBomText
    log.info("Bom created")
    bomText
  }
}
