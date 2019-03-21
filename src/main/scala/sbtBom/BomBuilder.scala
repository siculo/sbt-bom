package sbtBom

import scala.xml.Elem

class BomBuilder {
  val build: Elem =
    <bom xmlns="http://cyclonedx.org/schema/bom/1.0" version="1">
    </bom>
}
