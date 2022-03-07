# sbt-bom

*sbt bom.xml exporter*

The aim of this project is to:

- extract a valid [CycloneDx](https://cyclonedx.org/) bom file from sbt projects
- ensure that the bom file is processable with Software Composition Analysis tools (like Dependency Track) 

## how to test the plugin

see: ["exists" test project README.md](src/sbt-test/sbt-bom/exists/README.md)

## changelog

### v0.2.0
- The cyclonedx-core-java library has been integrated and is used to generate the BOM
- Removed all old model classes used so far

### v0.1.0
- First release
