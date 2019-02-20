package sbtBom

import scala.xml.{Elem, NodeBuffer, XML}

class BomBuilder {
  def build: Elem = {
    <bom xmlns="http://cyclonedx.org/schema/bom/1.0" version="1">
      <components>
      </components>
    </bom>
  }
}
