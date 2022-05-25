package io.github.siculo.sbtbom.creator

import io.github.siculo.sbtbom.ReportModel.{Dependency, License}
import io.github.siculo.sbtbom.TestLogger
import org.cyclonedx.CycloneDxSchema
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import sbt._

import scala.collection.JavaConverters._

class LibraryComponentCreatorSpec extends AnyWordSpec with Matchers {
  "schema 1.0 LibraryComponentCreator" should {

    val setup = BomCreatorSetup(
      schemaVersion = CycloneDxSchema.Version.VERSION_10,
      configuration = Compile,
      log = TestLogger
    )

    "create a component with a license idendified by id" in {
      val dependency = Dependency(
        "io.circe", "circe-core_2.13", "0.14.1", modified = false,
        Seq(License("Apache 2.0", Some("http://www.apache.org/licenses/LICENSE-2.0"))), filePaths = Seq.empty
      )
      val creator = new LibraryComponentCreator(setup, dependency)
      val componentLicenses = creator.create.getLicenseChoice.getLicenses.asScala
      componentLicenses.size shouldBe 1
      componentLicenses.head.getId shouldBe "Apache-2.0"
      componentLicenses.head.getName shouldBe null
    }

    "create a component with a license idendified by name" in {
      val dependency = Dependency(
        "io.circe", "circe-core_2.13", "0.14.1", modified = false,
        Seq(License("Unknown license", Some("http://host/path/to/license"))), filePaths = Seq.empty
      )
      val creator = new LibraryComponentCreator(setup, dependency)
      val componentLicenses = creator.create.getLicenseChoice.getLicenses.asScala
      componentLicenses.size shouldBe 1
      componentLicenses.head.getId shouldBe null
      componentLicenses.head.getName shouldBe "Unknown license"
    }
  }
}
