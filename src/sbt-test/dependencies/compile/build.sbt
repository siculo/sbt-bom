import java.io.File
import scala.xml.XML

lazy val root = (project in file("."))
  .settings(
    name := "dependencies",
    version := "0.1",
    libraryDependencies ++= Dependencies.library,
    bomFileName := "bom.xml",
    scalaVersion := "2.12.8",
    check := Def.sequential(
      Compile / clean,
      Compile / compile,
      checkTask
    ).value
  )

def copyTo(src: File, dest: String): java.nio.file.Path = {
  val srcPath = java.nio.file.Paths.get(src.getAbsolutePath)
  val destPath = java.nio.file.Paths.get(dest)
  java.nio.file.Files.copy(srcPath, destPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING)
}

lazy val check = taskKey[Unit]("check")
lazy val checkTask = Def.task {
  val s: TaskStreams = streams.value
  s.log.info("Verifying bom content...")
  val bomFile = makeBom.value
  val context = thisProject.value
  val expectedFile = file(s"${context.base}/etc/bom.xml")
  s.log.info(s"Comparing expected file ${bomFile.getAbsolutePath}")
  s.log.info(s"with actual file ${expectedFile.getAbsolutePath}")
  val expected = XML.loadFile(expectedFile)
  s.log.info(s"${bomFile.getPath}")
  val actual = XML.loadFile(bomFile)
  val expectedComponents = expected \ "components"
  val actualComponents = actual \ "components"
  require(expectedComponents == actualComponents, s"${context.id} is failed.")
  s.log.info(s"${bomFile.getPath} content verified")
}
