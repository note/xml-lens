import sbt._

// shadow sbt-scalajs' crossProject and CrossType until Scala.js 1.0.0 is released
import sbtcrossproject.{CrossType, crossProject}
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

object Dependencies {
  lazy val monocleVersion  = "1.5.0"
  lazy val scalaXmlVersion = "1.0.6"

  lazy val monocleCore = Def.setting("com.github.julien-truffaut"  %%% "monocle-core"   % monocleVersion)
  lazy val scalacheck  = Def.setting("org.scalacheck"              %%% "scalacheck"     % "1.13.5" % Test)
  lazy val monocleLaw  = Def.setting("com.github.julien-truffaut"  %%% "monocle-law"    % monocleVersion % "test")
  lazy val scalaTest   = Def.setting("org.scalatest"               %%% "scalatest"      % "3.0.4" % "test")

  lazy val scalaXml = "org.scala-lang.modules" %% "scala-xml"      % scalaXmlVersion
}