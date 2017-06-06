name := """xml-lens"""

version := "1.0"

scalaVersion := "2.12.2"

// Change this to another test framework if you prefer
libraryDependencies ++= Seq(
  "org.scala-lang.modules"      %% "scala-xml"      % "1.0.6",
  "org.scalaz"                  %% "scalaz-core"    % "7.2.10",
  "io.circe"                    %% "circe-parser"   % "0.7.0",
  "io.circe"                    %% "circe-optics"   % "0.7.0",
  "com.github.julien-truffaut"  %% "monocle-law"   % "1.4.0" % "test",
  "org.scalatest"               %% "scalatest"      % "3.0.1" % "test"
)

