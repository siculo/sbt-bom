package io.github.siculo.sbtbom.creator

import io.github.siculo.sbtbom.{ReportModel, TestLogger}
import org.cyclonedx.CycloneDxSchema
import org.cyclonedx.model.License
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import sbt.Compile

class LicenseCreatorSpec extends AnyWordSpec with Matchers {
  "schema 1.0 LicenseCreator" should {
    val setup = BomCreatorSetup(
      schemaVersion = CycloneDxSchema.Version.VERSION_10,
      configuration = Compile,
      log = TestLogger
    )
    val creator = new LicenseCreator(setup)

    "create a license idendified by id" in {
      val license: License = creator.create(
        ReportModel.License("Apache 2.0", Some("http://www.apache.org/licenses/LICENSE-2.0"))
      )
      license.getId shouldBe "Apache-2.0"
      license.getName shouldBe null
      license.getUrl shouldBe null
    }

    "create a license idendified by name" in {
      val license: License = creator.create(
        ReportModel.License("unknown license", Some("http://host/path"))
      )
      license.getId shouldBe null
      license.getName shouldBe "unknown license"
      license.getUrl shouldBe null
    }

    "create a license with relative path URI" in {
      val license: License = creator.create(
        ReportModel.License("EHCACHE-CORE-LICENSE", Some("src/assemble/EHCACHE-CORE-LICENSE.txt"))
      )
      license.getId shouldBe null
      license.getName shouldBe "EHCACHE-CORE-LICENSE"
      license.getUrl shouldBe null
    }

    "create a license with file URI" in {
      val license: License = creator.create(
        ReportModel.License("a license", Some("file:///path/license"))
      )
      license.getId shouldBe null
      license.getName shouldBe "a license"
      license.getUrl shouldBe null
    }
  }
}
