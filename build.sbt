import sbt.Keys.{libraryDependencies, version}
import Common._
import Dependencies._

lazy val ast = (project in file("ast"))
  .commonSettings
  .settings(
    name := "xml-lens-ast"
  )

lazy val testsCommon = (project in file("tests-common"))
  .commonSettings
  .settings(
    name := "xml-lens-tests-common",
    libraryDependencies ++= Seq(monocleLaw) // TODO: change to scalacheck if possible
  )
  .dependsOn(ast)

lazy val io = (project in file("io"))
  .commonSettings
  .settings(
    name := "xml-lens-io",
    libraryDependencies ++= Seq(scalaz, scalaTest)
  )
  .dependsOn(ast, testsCommon % "test->test")

lazy val optics = (project in file("optics"))
  .commonSettings
  .settings(
    name := "xml-lens-optics",
    libraryDependencies ++= Seq(monocleCore, monocleLaw, scalaTest)
  )
  .dependsOn(ast, testsCommon % "test->test", io % "test->test")

lazy val bench = (project in file("bench"))
  .commonSettings
  .settings(
    name := "xml-lens-bench",
    libraryDependencies ++= Seq(scalaXml, scalaTest),
    scalacOptions += "-Xlint:_,-missing-interpolator"
  )
  .enablePlugins(JmhPlugin)
  .dependsOn(optics, io)

lazy val root = (project in file("."))
  .commonSettings
  .aggregate(io, optics)
