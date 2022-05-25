package io.github.siculo.sbtbom

import io.github.siculo.sbtbom.licenses.Model._
import io.github.siculo.sbtbom.licenses.{LicenseArchive, LicenseArchiveParser}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.io.Source

class LicenseArchiveSpec extends AnyWordSpec with Matchers {

  private val licensesJson =
    """{
      |  "licenseListVersion": "65dd995",
      |  "licenses": [
      |    {
      |      "reference": "https://spdx.org/licenses/CC-BY-SA-2.1-JP.html",
      |      "isDeprecatedLicenseId": false,
      |      "detailsUrl": "https://spdx.org/licenses/CC-BY-SA-2.1-JP.json",
      |      "referenceNumber": 0,
      |      "name": "Creative Commons Attribution Share Alike 2.1 Japan",
      |      "licenseId": "CC-BY-SA-2.1-JP",
      |      "seeAlso": [
      |        "https://creativecommons.org/licenses/by-sa/2.1/jp/legalcode"
      |      ],
      |      "isOsiApproved": false
      |    }
      |  ]
      |}""".stripMargin

  "LicensesArchiveParser" should {
    "fail parsing a not valid json" in {
      val parser = new LicenseArchiveParser("")
      parser.valid shouldBe false
    }

    "fail parsing a not valid archive" in {
      val parser = new LicenseArchiveParser("{}")
      parser.valid shouldBe false
    }

    "parse a valid archive" in {
      val fileStream = getClass.getResourceAsStream("/licenses.json")
      val archiveText = Source.fromInputStream(fileStream).mkString
      new LicenseArchiveParser(archiveText).valid shouldBe true
    }

    "extract no lincense archive" in {
      val archive: Option[Licenses] = new LicenseArchiveParser("{}").parsedArchive
      archive shouldBe None
    }

    "estract a license archive" in {
      new LicenseArchiveParser(licensesJson).parsedArchive shouldBe Some(
        Licenses(
          licenseListVersion = "65dd995",
          licenses = Seq(
            License(
              reference = "https://spdx.org/licenses/CC-BY-SA-2.1-JP.html",
              isDeprecatedLicenseId = false,
              detailsUrl = "https://spdx.org/licenses/CC-BY-SA-2.1-JP.json",
              referenceNumber = 0,
              name = "Creative Commons Attribution Share Alike 2.1 Japan",
              licenseId = "CC-BY-SA-2.1-JP",
              seeAlso = Seq("https://creativecommons.org/licenses/by-sa/2.1/jp/legalcode"),
              isOsiApproved = false
            )
          )
        )
      )
    }
  }

  "empty LicensesArchive" should {
    "have no license" in {
      val emptyArchive = new LicenseArchive(new LicenseArchiveParser("{}").parsedArchive)
      emptyArchive.allLicenses shouldBe empty
    }
  }

  "LicensesArchive" should {
    "have some licenses" in {
      LicenseArchive.current.allLicenses should not be empty
    }

    "find no license by id" in {
      LicenseArchive.current.findById("wrong id") shouldBe empty
    }

    "find licenses by id" in {
      LicenseArchive.current.findById("ECL-2.0") should not be empty
    }

    "find no license by url" in {
      LicenseArchive.current.findByUrlIgnoringProtocol("http://host/unknown") shouldBe empty
    }

    "find license by url" in {
      LicenseArchive.current.findByUrlIgnoringProtocol("https://opensource.org/licenses/MPL-2.0") should not be empty
    }

    "find license by http url" in {
      LicenseArchive.current.findByUrlIgnoringProtocol("http://opensource.org/licenses/MPL-2.0") should not be empty
    }

    "find the right license" in {
      LicenseArchive.current.findByUrlIgnoringProtocol("https://www.gnu.org/software/classpath/license.html")
        .map(_.licenseId) shouldBe Some("GPL-2.0-with-classpath-exception")
    }

    "find the right license using more than one url" in {
      LicenseArchive.current.findByUrlIgnoringProtocol(
        //"https://ptolemy.berkeley.edu/XXcopyright.htm",
        "https://fedoraproject.org/wiki/Licensing:MIT#Modern_Variants",
        //"https://pirlwww.lpl.arizona.edu/XXresources/guide/software/PerlTk/Tixlic.html"
      )
        .map(_.licenseId) shouldBe Some("MIT-Modern-Variant")

    }
  }
}
