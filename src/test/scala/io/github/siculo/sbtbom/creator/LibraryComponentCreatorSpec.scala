package io.github.siculo.sbtbom.creator

import io.github.siculo.sbtbom.ReportModel.{Dependency, License}
import io.github.siculo.sbtbom.TestLogger
import org.cyclonedx.CycloneDxSchema
import org.cyclonedx.model.Component
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import sbt._

class LibraryComponentCreatorSpec extends AnyWordSpec with Matchers {
  "schema 1.0 LibraryComponentCreator" should {
    val setup = BomCreatorSetup(
      schemaVersion = CycloneDxSchema.Version.VERSION_10,
      configuration = Compile,
      log = TestLogger
    )

    val creator = new LibraryComponentCreator(setup)

    "create a library component" in {
      val dependency = Dependency(
        "io.circe", "circe-core_2.13", "0.14.1", modified = false,
        Seq(License("Apache 2.0", Some("http://www.apache.org/licenses/LICENSE-2.0"))), filePaths = Seq.empty
      )
      val component: Component = creator.create(dependency)
      component.getType shouldBe Component.Type.LIBRARY
    }

    "create a component with group, name and version" in {
      val dependency = Dependency(
        "io.circe", "circe-core_2.13", "0.14.1", modified = false,
        Seq(License("Apache 2.0", Some("http://www.apache.org/licenses/LICENSE-2.0"))), filePaths = Seq.empty
      )
      val component: Component = creator.create(dependency)
      component.getGroup shouldBe "io.circe"
      component.getName shouldBe "circe-core_2.13"
      component.getVersion shouldBe "0.14.1"
    }

    "create a component with modified flag set to false" in {
      val dependency = Dependency(
        "io.circe", "circe-core_2.13", "0.14.1", modified = false,
        Seq(License("Apache 2.0", Some("http://www.apache.org/licenses/LICENSE-2.0"))), filePaths = Seq.empty
      )
      val component: Component = creator.create(dependency)
      component.getModified shouldBe false
    }
  }
}
