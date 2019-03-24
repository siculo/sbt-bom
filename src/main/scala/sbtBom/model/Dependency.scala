package sbtBom.model

import java.io.File

case class Dependency(group: String,
                      name: String,
                      version: String,
                      modified: Boolean,
                      licenses: Seq[License] = Seq(),
                      file: Option[File] = None)
