# Exists

*sample sbt-bom project*

### how to run the project and test sbt-bom plugin

First you have to deploy locally the plugin. To do this use the command line.
Go to the main project directory and run sbt with the *publishLocal* command:

> sbt publishLocal

The plug in will be published in the local ivy repository. Take note of the
version published or read the *version* setting inside *build.sbt*

Then, within the command line go to the test project and launch sbt, setting
the *plugin.version* property:

> sbt -Dplugin.version=0.1.0-SNAPSHOT

To generate the bom file use sbt command:

> makeBom

Generated *bom.xml* file is generated inside the target directory of the test
project