package io.github.siculo.sbtbom.licenses

import play.api.libs.json.{Format, Json}

object Model {
  case class License(reference: String,
                     isDeprecatedLicenseId: Boolean,
                     detailsUrl: String,
                     referenceNumber: Int,
                     name: String,
                     licenseId: String,
                     seeAlso: Seq[String],
                     isOsiApproved: Boolean) {
    def isReferencedByUrl(url: String): Boolean =
      seeAlso.exists {
        licenseRef =>
          url.contains(licenseRef)
      }
  }

  case class Licenses(licenseListVersion: String, licenses: Seq[License])

  implicit val licenseFormat: Format[License] = Json.format[License]
  implicit val licensesFormat: Format[Licenses] = Json.format[Licenses]
}
