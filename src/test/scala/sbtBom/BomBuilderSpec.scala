package sbtBom

import java.io.File
import java.net.URL

import org.scalatest.{Assertion, Matchers, WordSpec}
import sbtBom.LicensesArchive.getClass
import sbtBom.model.License

import scala.xml.{Elem, PrettyPrinter}

class BomBuilderSpec extends WordSpec with Matchers {
  /*
    x schema and version
    x licenses
      x name
      x id / Apache-2.0
    x hashes
    - description, publisher
    - conver from dependency report to abstract dependecies (use mocks?)
   */

  "BomBuilder" should {
    "produce an xml report with some dependencies" in {
      val builder = new BomBuilder(dependencies :+ jackson :+ pivotal :+ esapi)
      println(builder.build)
      builder.build shouldBeSameXml bom
    }
  }

  val jackson = model.Dependency(
    group = "org.codehaus.jackson",
    name = "jackson-jaxrs",
    version = "1.9.13",
    modified = false,
    file = getResourceFile("/jackson.txt"),
  )

  val pivotal = model.Dependency(
    group = "org.springframework.boot",
    name = "spring-boot-legacy",
    version = "1.0.1.RELEASE",
    modified = true,
    licenses = Seq(License(id = Some("Apache-2.0"), name = Some("Apache 2.0"))),
    file = getResourceFile("/pivotal.txt"),
  )

  val esapi = model.Dependency(
    group = "org.owasp.esapi",
    name = "esapi",
    version = "2.0GA",
    modified = false,
    licenses = Seq(
      License(name = Some("BSD")),
      License(name = Some("Creative Commons 3.0 BY-SA")),
    )
  )

  val dependencies = model.Dependencies()

  val bom =
    <bom xmlns="http://cyclonedx.org/schema/bom/1.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1" xsi:schemaLocation="http://cyclonedx.org/schema/bom/1.0 http://cyclonedx.org/schema/bom/1.0">
    <components>
      <component type="library">
        <group>{jackson.group}</group>
        <name>{jackson.name}</name>
        <version>{jackson.version}</version>
        <modified>{jackson.modified}</modified>
        <hashes>
          <hash alg="MD5">7cb6cc60cda9078bcbe999e8cdf14205</hash>
          <hash alg="SHA-1">ba568f5409525999f925bd2cad1c8c2ba183816e</hash>
          <hash alg="SHA-256">7b69afd8fdd8683d945ee55a8993b32debe49a905ccea2955a6d56ff5cc9fd24</hash>
          <hash alg="SHA-384">21a0e2c090381cf78b6b2e9291609d8a487c0fbd3c1c65a05723c8f1e3fccd0a7a085977a2b7c90325ab8ada08508ed8</hash>
          <hash alg="SHA-512">6ba1ec55d95cfb5689d78e3f02316842b50fa5b47f77647efca8e04dc0f9d765e49f84f4ee51e798e3270518fd7668bb9f4fd3d3315ff2791f22a560c58afd9d</hash>
        </hashes>
      </component>
      <component type="library">
        <group>{pivotal.group}</group>
        <name>{pivotal.name}</name>
        <version>{pivotal.version}</version>
        <modified>{pivotal.modified}</modified>
        <licenses>
          <license>
            <id>{pivotal.licenses.head.id.get}</id>
            <name>{pivotal.licenses.head.name.get}</name>
          </license>
        </licenses>
        <hashes>
          <hash alg="MD5">c71e915565fd95c761c0ad149e6b6b8a</hash>
          <hash alg="SHA-1">37b2c98f8a4fc17bb4c4590e82755adf002efa27</hash>
          <hash alg="SHA-256">6b3ed59aca2e75c939785d28b9ad20bfcaac8eeed99ef584173dc3febf6fdc24</hash>
          <hash alg="SHA-384">4b516c7cde77b067268a7364b68f371ba6548381a5fdbfa369047059067a810f6c1e556c69eb8b39b11319eda145b0ce</hash>
          <hash alg="SHA-512">84d74155ff947e60da72bbc00af8f977a61c0c717a8c10eb61eecfde8dde2d782712ebbcc56b6f7fcb98ae9b2198e0b7aceed310fcb6c2813448336dbc05dae4</hash>
        </hashes>
      </component>
      <component type="library">
        <group>{esapi.group}</group>
        <name>{esapi.name}</name>
        <version>{esapi.version}</version>
        <modified>{esapi.modified}</modified>
        <licenses>
          <license>
            <name>{esapi.licenses(0).name.get}</name>
          </license>
          <license>
            <name>{esapi.licenses(1).name.get}</name>
          </license>
        </licenses>
      </component>
    </components>
  </bom>

  private def getResourceFile(resourcePath: String) = {
    getClass.getResource(resourcePath) match {
      case null => None
      case url => Some(new File(url.getPath))
    }
  }

  implicit class ElemShouldWrapper(elem: Elem) {
    import scala.xml.Utility.trim

    private val printer = new PrettyPrinter(80, 2)
    def shouldBeSameXml(that: Elem): Assertion =
      printer.format(trim(elem)) shouldBe printer.format(trim(that))
  }
}
