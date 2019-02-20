sys.props.get("plugin.version") match {
  case Some(version) => addSbtPlugin("sbtBom" % "sbt-bom" % version)
  case _ => sys.error("missing system property 'plugin.version'")
}
