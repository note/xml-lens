import sbt._

object Dependencies {
  lazy val monocleVersion  = "1.4.0"
  lazy val scalaXmlVersion = "1.0.6"

  lazy val monocleCore = "com.github.julien-truffaut"  %% "monocle-core"   % monocleVersion
  lazy val scalaXml    = "org.scala-lang.modules"      %% "scala-xml"      % scalaXmlVersion
  lazy val scalacheck  = "org.scalacheck"              %% "scalacheck"     % "1.13.4" % Test
  lazy val monocleLaw  = "com.github.julien-truffaut"  %% "monocle-law"    % monocleVersion % "test"
  lazy val scalaTest   = "org.scalatest"               %% "scalatest"      % "3.0.4" % "test"

  lazy val deps = Seq(monocleCore, monocleLaw, scalaTest)
}