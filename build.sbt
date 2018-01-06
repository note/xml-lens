import sbt.Keys.{libraryDependencies, scalacOptions, _}
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
    libraryDependencies ++= Seq(scalacheck, scalaTest)
  )
  .dependsOn(ast)

lazy val io = (project in file("io"))
  .commonSettings
  .settings(
    name := "xml-lens-io"
  )
  .dependsOn(ast, testsCommon % "test->test")

lazy val optics = (project in file("optics"))
  .commonSettings
  .settings(
    name := "xml-lens-optics",
    libraryDependencies ++= Seq(monocleCore, monocleLaw)
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

lazy val examples = (project in file("examples"))
  .commonSettings
  .settings(
    name := "xml-lens-examples",
    libraryDependencies ++= Seq(scalaXml, scalaTest),
    scalacOptions += "-Xlint:_,-missing-interpolator"
  )
  .enablePlugins(JmhPlugin)
  .dependsOn(optics, io)

lazy val docSettings = Seq(
  micrositeName := "xml-lens",
  micrositeDescription := "XML Optics library for Scala",
  micrositeHomepage := "http://note.github.io/xml-lens",
  micrositeBaseUrl := "/xml-lens",
  micrositeDocumentationUrl := "/xml-lens/api",
  micrositeGithubOwner := "note",
  micrositeGithubRepo := "xml-lens",
  micrositeGitterChannel := false,
  autoAPIMappings := true,
  unidocProjectFilter in (ScalaUnidoc, unidoc) := inProjects(ast, optics),
  siteSubdirName in ScalaUnidoc := "api",
  addMappingsToSiteDir(mappings in (ScalaUnidoc, packageDoc), siteSubdirName in ScalaUnidoc),
  ghpagesNoJekyll := false,
  fork in tut := true,
  fork in (ScalaUnidoc, unidoc) := true,
  git.remoteRepo := "git@github.com:note/xml-lens.git",
  includeFilter in makeSite := "*.html" | "*.css" | "*.png" | "*.jpg" | "*.gif" | "*.js" | "*.swf" | "*.yml" | "*.md" | "*.svg",
  micrositeDataDirectory := (resourceDirectory in Compile).value / "microsite" / "data",
  micrositePushSiteWith := GitHub4s,
  micrositeGithubToken := sys.env.get("GITHUB_TOKEN")
)


lazy val docs = (project in file("docs"))
  .commonSettings
  .settings(
    name := "xml-lens-docs"
  )
  .settings(docSettings)
  .settings(scalacOptions in Tut := (scalacOptions in Tut).value.filterNot(Set("-Ywarn-unused-import", "-Xfatal-warnings", "-Xlint")))
  .enablePlugins(GhpagesPlugin)
  .enablePlugins(MicrositesPlugin)
  .enablePlugins(ScalaUnidocPlugin)
  .dependsOn(ast, io, optics)

lazy val root = (project in file("."))
  .commonSettings
  .settings(
    name := "xml-lens"
  )
  .aggregate(io, optics)
