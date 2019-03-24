package sbtBom.model

case class License(id: Option[String] = None,
                   name: Option[String] = None,
                   references: Seq[String] = Seq())
