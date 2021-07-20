# Exists

*sample sbt-bom project*

### how to run the project

First you have to deploy locally the plugin. To do this use the command line.
Go to the main project directory and run sbt with the *publishLocal* command:

> sbt publishLocal

The plug in will be published in the local ivy repository. Take note of the
version published or read the *version* setting inside *build.sbt*

Then, within the command line go to the test project and launch sbt, setting
the *plugin.version* property:

> sbt -Dplugin.version=0.1.0-SNAPSHOT
