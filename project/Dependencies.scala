import sbt._

object Dependencies {
  lazy val monocleVersion = "1.4.0"

  lazy val monocleCore = "com.github.julien-truffaut"  %% "monocle-core"   % monocleVersion
  lazy val monocleLaw  = "com.github.julien-truffaut"  %% "monocle-law"    % monocleVersion % "test"
  lazy val scalaTest   = "org.scalatest"               %% "scalatest"      % "3.0.3" % "test"

  lazy val deps = Seq(monocleCore, monocleLaw, scalaTest)
}