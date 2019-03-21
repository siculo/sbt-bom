package sbtBom

import org.scalatest.{Matchers, WordSpec}

import scala.xml.Elem

class BomBuilderSpec extends WordSpec with Matchers {

  "BomBuilder" should {
    "produce an xml report" in {
      val builder = new BomBuilder()
      val root: Elem = builder.build
      root.label shouldBe "bom"
    }
  }

}
