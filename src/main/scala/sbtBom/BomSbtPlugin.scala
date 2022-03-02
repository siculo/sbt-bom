package sbtBom

import sbt._

/**
 * plugin object
 */
object BomSbtPlugin extends AutoPlugin {

  override def trigger: PluginTrigger = allRequirements

  object autoImport extends BomSbtKeys

  override lazy val projectSettings: Seq[Setting[_]] = BomSbtSettings.projectSettings

}
