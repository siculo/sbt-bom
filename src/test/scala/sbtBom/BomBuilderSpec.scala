package sbtBom

import org.scalatest.Assertion
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import sbtBom.model.LicenseId

import java.io.File
import scala.xml.{Node, NodeSeq, PrettyPrinter}

/*
  todo customize xml matching in the right way
 */
class BomBuilderSpec extends AnyWordSpec with Matchers {

  import BomBuilderSpec._

  "bom" should {
    "have a root with all required properties" in {
      val rootWithoutContent = root.copy(child = Seq())
      rootWithoutContent shouldBeSameXml
        <bom
        xmlns="http://cyclonedx.org/schema/bom/1.0"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        version="1"
        xsi:schemaLocation="http://cyclonedx.org/schema/bom/1.0 http://cyclonedx.org/schema/bom/1.0">
        </bom>
    }

    "contains all library components" in {
      allLibraryComponents.foreach(
        _.attribute("type").get.text shouldBe "library"
      )
      allLibraryComponents.size shouldBe 3
    }
  }

  "bom component" should {
    "have a group propety" in {
      (jacksonComponent \ "group").text shouldBe jackson.group
    }

    "have a name propety" in {
      (jacksonComponent \ "name").text shouldBe jackson.name
    }

    "have a version property" in {
      (jacksonComponent \ "version").text shouldBe jackson.version
    }

    "have a modified property" in {
      (pivotalComponent \ "modified").text shouldBe "true"
      (jacksonComponent \ "modified").text shouldBe "false"
    }

    // todo: review hash generation to avoid unpredictable values
    "have hashes properties" ignore {
      println(jacksonComponent \ "hashes")
      jacksonComponent \ "hashes" shouldBeSameXml
        <hashes>
          <hash alg="MD5">7cb6cc60cda9078bcbe999e8cdf14205</hash>
          <hash alg="SHA-1">ba568f5409525999f925bd2cad1c8c2ba183816e</hash>
          <hash alg="SHA-256">7b69afd8fdd8683d945ee55a8993b32debe49a905ccea2955a6d56ff5cc9fd24</hash>
          <hash alg="SHA-512">6ba1ec55d95cfb5689d78e3f02316842b50fa5b47f77647efca8e04dc0f9d765e49f84f4ee51e798e3270518fd7668bb9f4fd3d3315ff2791f22a560c58afd9d</hash>
        </hashes>
    }

    "have no hashes properties" in {
      val hashes = esapiComponent \ "hashes"
      hashes.size shouldBe 0
    }

    "have a license with name and id" in {
      pivotalComponent \ "licenses" shouldBeSameXml
        <licenses>
          <license>
            <name>Apache-2.0</name>
          </license>
        </licenses>
    }

    "have two licenses with name" in {
      esapiComponent \ "licenses" shouldBeSameXml
        <licenses>
          <license>
            <name>BSD</name>
          </license>
          <license>
            <name>Creative Commons 3.0 BY-SA</name>
          </license>
        </licenses>
    }
  }

  import scala.xml.Utility.trim

  implicit class ElemShouldWrapper(node: Node) {
    def shouldBeSameXml(that: Node): Assertion =
      printer.format(trim(node)) shouldBe printer.format(trim(that))
  }

  implicit class ElemSeqShouldWrapper(ns: NodeSeq) {
    def shouldBeSameXml(that: Node): Assertion = {
      ns.size shouldBe 1
      printer.format(trim(ns.head)) shouldBe printer.format(trim(that))
    }
  }
}

object BomBuilderSpec {
  private val jackson = model.Module(group = "org.codehaus.jackson", name = "jackson-jaxrs", version = "1.9.13", modified = false, file = getResourceFile("/jackson.txt"))

  private val pivotal = model.Module(group = "org.springframework.boot", name = "spring-boot-legacy", version = "1.0.1.RELEASE", modified = true, licenseIds = Seq(
    LicenseId(name = "Apache-2.0", url = None)
  ), file = getResourceFile("/pivotal.txt"))

  private val esapi = model.Module(group = "org.owasp.esapi", name = "esapi", version = "2.0GA", modified = false, licenseIds = Seq(
    LicenseId(name = "BSD", url = None),
    LicenseId(name = "Creative Commons 3.0 BY-SA", url = None),
  ))

  private val printer = new PrettyPrinter(80, 2)

  private val dependencies = model.Modules() :+ jackson :+ pivotal :+ esapi
  private val builder = new BomBuilder(dependencies)
  private val root = builder.build

  private val allLibraryComponents = root \ "components" \ "component"

  private val jacksonComponent = allLibraryComponents.head
  private val pivotalComponent = allLibraryComponents(1)
  private val esapiComponent = allLibraryComponents(2)

  private def getResourceFile(resourcePath: String) = {
    getClass.getResource(resourcePath) match {
      case null => None
      case url => Some(new File(url.getPath))
    }
  }
}