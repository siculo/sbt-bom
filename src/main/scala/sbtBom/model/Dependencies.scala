package sbtBom.model

class Dependencies private(val all: Seq[Dependency]) {
  def :+(dependency: Dependency) = new Dependencies(all :+ dependency)
}

object Dependencies {
  def apply(): Dependencies = new Dependencies(Seq())
}
