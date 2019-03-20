package sbtBom

import scala.util.Try
import scala.xml.{Node, XML}

class LicensesArchiveParser(textArchive: String) {
  private lazy val xmlTry = Try {
    XML.loadString(textArchive)
  }

  private lazy val licensesTry: Try[Seq[License]] =
    xmlTry.map { root =>
      (root \ "licenses").map { license =>
        val licenseId = (license \ "licenseId").text
        val refs = (license \ "seeAlso").map(_.text)
        License(licenseId, refs)
      }
    }

  lazy val licenses = licensesTry.getOrElse(Seq())

  lazy val isValid = licensesTry.isSuccess
}
