package io.github.siculo.sbtbom.creator

import io.github.siculo.sbtbom.ReportModel
import io.github.siculo.sbtbom.licenses.LicenseArchive
import org.cyclonedx.CycloneDxSchema
import org.cyclonedx.model.{License, LicenseChoice}

class LicenseCreator(setup: BomCreatorSetup) {
  def create(modelLicense: ReportModel.License): License = {
    val license = new License()
    modelLicense.url.foreach {
      licenseUrl =>
        LicenseArchive.current.findByUrlIgnoringProtocol(licenseUrl).foreach {
          archiveLicense =>
            license.setId(archiveLicense.licenseId)
        }
        if (setup.schemaVersion != CycloneDxSchema.Version.VERSION_10) {
          license.setUrl(licenseUrl)
        }
    }
    if (license.getId == null) {
      license.setName(modelLicense.name)
    }
    license
  }
}
