# sbt-bom

*sbt bom.xml exporter*

The aim of this project is to:

- extract a valid [CycloneDx](https://cyclonedx.org/) bom file from sbt projects
- ensure that the bom file is processable with Software Composition Analysis tools (like Dependency Track)

## usage

## testing

There are two types of test: unit test done with scalatest and scripted test

### unit test

Unit tests are written using scalatest syntax. Only pure logic classes are tested using these tests.

To run unit tests use the `test` command to run all tests, or `testOnly ...` command specifying the list of test to be
executed.

### scripted tests

[Scripted](https://www.scala-sbt.org/1.x/docs/Testing-sbt-plugins.html) is a tool that allow you to test sbt plugins.
For each test it is necessary to create a specially crafted project. These projects are inside src/sbt-test directory.

Scripted tests are run using `scripted` comand.

## changelog

### v0.2.0
- The cyclonedx-core-java library has been integrated and is used to generate the BOM
- Removed all old model classes used so far

### v0.1.0
- First release
