import sbt.Keys.version
import Dependencies._

lazy val root = (project in file(".")).settings(
  name := """xml-lens""",
  version := "0.1.0",
  scalaVersion := "2.12.2",
  crossScalaVersions := Seq("2.12.2", "2.11.11"),
  libraryDependencies ++= deps,
  scalacOptions ++= Seq(
    "-target:jvm-1.8",
    "-encoding", "UTF-8",
    "-unchecked",
    "-deprecation",
    "-feature",
    "-language:existentials",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-Yno-adapted-args",
    "-Ywarn-dead-code",
    "-Ywarn-infer-any",
    "-Ywarn-unused-import",
    "-Xfatal-warnings",
    "-Xlint"
  ),
  coverageHighlighting := true,
  coverageScalacPluginVersion := "1.3.0"
)
