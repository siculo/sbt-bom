package sbtBom.model

case class Dependencies (all: Seq[Dependency]) {
  def :+(dependency: Dependency) = new Dependencies(all :+ dependency)
}

object Dependencies {
  def apply(): Dependencies = new Dependencies(Seq())
}
