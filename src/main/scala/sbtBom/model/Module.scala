package sbtBom.model

import java.io.File

case class Module(group: String,
                  name: String,
                  version: String,
                  modified: Boolean,
                  licenseIds: Seq[LicenseId] = Seq(),
                  file: Option[File] = None)
