import sbt._
import sbt.Keys._
import scoverage.ScoverageKeys.{coverageHighlighting, coverageScalacPluginVersion}

object Common {
  val commonScalacOptions = Seq(
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
  )

  val commonScalaVersion =  "2.12.2"
  val commonCrossScalaVersions = Seq("2.12.2", "2.11.11")

  implicit class ProjectFrom(project: Project) {
    def commonSettings: Project = project.settings(
      scalacOptions ++= commonScalacOptions,
      scalaVersion := commonScalaVersion,
      crossScalaVersions := commonCrossScalaVersions,
      coverageHighlighting := true,
      coverageScalacPluginVersion := "1.3.0",
      version := "0.1.0"
    )
  }

}