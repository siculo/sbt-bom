package sbtBom.model

case class Modules(all: Seq[Module]) {
  def :+(module: Module) = new Modules(all :+ module)
}

object Modules {
  def apply(): Modules = new Modules(Seq())
}
