package sbtBom.model

case class LicenseId(name: String, url: Option[String])

object LicenseId {
  def apply(licenseId: (String, Option[String])): LicenseId =
    licenseId match {
      case (name, url) => LicenseId(name, url)
    }
}
