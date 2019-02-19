package sbtBom

import sbt.Keys._
import sbt._

object BomSbtPlugin extends AutoPlugin {

  object autoImport {
    val bomTest = TaskKey[String](
      "bom-test",
      "Sample task"
    )
  }

  import autoImport._

  override lazy val projectSettings = Seq(
    bomTest := {
      streams.value.log.info("BOM Test")
      "BOM Test"
    }
  )

}
