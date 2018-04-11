import sbt._
import sbt.Keys._
import sbtcrossproject.CrossProject
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
  val commonCrossScalaVersions = Seq("2.12.4", "2.11.12")

  implicit class CrossProjectFrom(project: CrossProject) {
    def commonSettings: CrossProject = project.settings(_commonSettings)
  }

  implicit class ProjectFrom(project: Project) {
    def commonSettings: Project = project.settings(_commonSettings)
  }

  private val _commonSettings = Seq(
    scalacOptions ++= commonScalacOptions,
    scalacOptions in (Compile, console) ~= {
      _.filterNot(Set("-Ywarn-unused-import")).map {
        case "-Xlint" => "-Xlint:-unused,_"
        case another => another
      }
    },
    scalacOptions in (Test, console) ~= {
      _.filterNot(Set("-Ywarn-unused-import")).map {
        case "-Xlint" => "-Xlint:-unused,_"
        case another => another
      }
    },
    scalaVersion := commonScalaVersion,
    crossScalaVersions := commonCrossScalaVersions,
    coverageHighlighting := true,
    coverageScalacPluginVersion := "1.3.0"
  )

}