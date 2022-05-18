package io.github.siculo.sbtbom

class ListBomTask(params: TaskParams) extends BomTask[String](params) {
  override def execute: String = {
    log.info("Creating bom")
    val bomText = getBomText
    log.info("Bom created")
    bomText
  }
}
