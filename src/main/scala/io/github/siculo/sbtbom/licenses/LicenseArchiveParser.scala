package io.github.siculo.sbtbom.licenses

import io.github.siculo.sbtbom.licenses.Model._
import play.api.libs.json._

import scala.util.Try

class LicenseArchiveParser(archiveText: String) {
  private lazy val parsedArchiveTry: Try[Licenses] =
    Try {
      Json.parse(archiveText)
    }.flatMap {
      jsValue =>
        JsResult.toTry(Json.fromJson[Licenses](jsValue))
    }

  lazy val valid: Boolean = parsedArchiveTry.isSuccess

  lazy val parsedArchive: Option[Licenses] = parsedArchiveTry.toOption
}
