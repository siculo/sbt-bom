package io.github.siculo.sbtbom

class ListBomTask(properties: BomTaskProperties) extends BomTask[String](properties) {
  override def execute: String = {
    log.info("Creating bom")
    val bomText = getBomText
    log.info("Bom created")
    bomText
  }
}
