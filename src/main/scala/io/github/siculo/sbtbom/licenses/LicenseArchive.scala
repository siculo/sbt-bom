package io.github.siculo.sbtbom.licenses

import io.github.siculo.sbtbom.licenses.Model._

import java.net.{URI, URL}
import scala.io.Source
import scala.util.Try

/**
 * @param archive None when there is no valid license archive; Some(licenses) if a valid archive exists.
 */
class LicenseArchive(archive: Option[Licenses]) {
  lazy val allLicenses: Seq[License] =
    archive match {
      case Some(licenseArchive) => licenseArchive.licenses
      case _ => Seq.empty
    }

  def findById(id: String): Option[License] =
    findByCriteria {
      license => license.licenseId == id
    }

  def findByURI(uri: URI*): Option[License] = {
    val urlToBeFound = uri.map(_.toString)
    findByCriteria {
      license =>
        urlToBeFound.exists(url => license.isReferencedByUrl(url))
    }
  }

  def findByUrlIgnoringProtocol(url: String*): Option[License] =
    findByURIIgnoringProtocol(getAllValidURI(url: _*): _*)

  def findByURIIgnoringProtocol(uri: URI*): Option[License] =
    findByURI(uri.flatMap(u => allProtocolURI(u)): _*)

  private def getAllValidURI(url: String*): Seq[URI] =
    url.map {
      u =>
        Try(new URI(u))
    }.filter(_.isSuccess).map(_.get)

  private def allProtocolURI(uri: URI): Seq[URI] =
    Seq(
      new URI("http", uri.getUserInfo, uri.getHost, uri.getPort, uri.getPath, uri.getQuery, uri.getFragment),
      new URI("https", uri.getUserInfo, uri.getHost, uri.getPort, uri.getPath, uri.getQuery, uri.getFragment)
    )

  def findByCriteria(criteria: License => Boolean): Option[License] =
    allLicenses.find(criteria)
}

object LicenseArchive {
  private lazy val fileStream = getClass.getResourceAsStream("/licenses.json")
  private lazy val archiveText = Source.fromInputStream(fileStream).mkString

  val current: LicenseArchive = new LicenseArchive(new LicenseArchiveParser(archiveText).parsedArchive)
}
