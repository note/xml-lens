import sbt.Keys.{libraryDependencies, scalacOptions, _}
import Common._
import Dependencies._
// shadow sbt-scalajs' crossProject and CrossType until Scala.js 1.0.0 is released
import sbtcrossproject.{crossProject, CrossType}

organization in ThisBuild := "pl.msitko"

lazy val ast = (crossProject(JSPlatform, JVMPlatform).crossType(CrossType.Pure) in file("ast"))
  .commonSettings
  .settings(
    name := "xml-lens-ast"
  )
  .settings(publishSettings)

lazy val astJVM = ast.jvm
lazy val astJS  = ast.js

lazy val testsCommon = (crossProject(JSPlatform, JVMPlatform).crossType(CrossType.Pure) in file("tests-common"))
  .commonSettings
  .settings(
    name := "xml-lens-tests-common",
    libraryDependencies ++= Seq(scalacheck.value, scalaTest.value)
  )
  .dependsOn(ast)

lazy val testsCommonJVM = testsCommon.jvm
lazy val testsCommonJS  = testsCommon.js

lazy val io = (crossProject(JSPlatform, JVMPlatform).crossType(CrossType.Full) in file("io"))
  .commonSettings
  .settings(
    name := "xml-lens-io"
  )
  .settings(publishSettings)
  .jsConfigure(_.enablePlugins(ScalaJSBundlerPlugin))
  .jsSettings(
    npmDependencies in Compile ++= Seq(
      "@msitko/sax"         -> "1.2.5-4"
    ),
    // with `-Ywarn-dead-code` enabled `var onerror: js.Function1[js.Any, Unit] = js.native` fails
    scalacOptions  -= "-Ywarn-dead-code",
    libraryDependencies += "com.lihaoyi" %%% "fastparse" % "1.0.0",
    coverageEnabled := false
  )
  .dependsOn(ast, testsCommon % "test->test")

lazy val ioJVM = io.jvm
lazy val ioJS  = io.js

lazy val optics = (crossProject(JSPlatform, JVMPlatform).crossType(CrossType.Full) in file("optics"))
  .commonSettings
  .settings(
    name := "xml-lens-optics",
    libraryDependencies ++= Seq(monocleCore.value, monocleLaw.value)
  )
  .settings(publishSettings)
  .jsConfigure(_.enablePlugins(ScalaJSBundlerPlugin))
  .jsSettings(
    coverageEnabled := false
  )
  .dependsOn(ast, testsCommon % "test->test", io % "test->test")

lazy val opticsJVM = optics.jvm
lazy val opticsJS  = optics.js

lazy val bench = (project in file("bench"))
  .commonSettings
  .settings(
    name := "xml-lens-bench",
    libraryDependencies ++= Seq(scalaXml, scalaTest.value),
    scalacOptions += "-Xlint:_,-missing-interpolator"
  )
  .settings(noPublishSettings)
  .enablePlugins(JmhPlugin)
  .dependsOn(opticsJVM, ioJVM)

lazy val examples = (project in file("examples"))
  .commonSettings
  .settings(
    name := "xml-lens-examples",
    libraryDependencies ++= Seq(scalaXml, scalaTest.value),
    scalacOptions += "-Xlint:_,-missing-interpolator"
  )
  .settings(noPublishSettings)
  .enablePlugins(JmhPlugin)
  .dependsOn(opticsJVM, ioJVM)

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
  unidocProjectFilter in (ScalaUnidoc, unidoc) := inProjects(astJVM, opticsJVM),
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
  .settings(noPublishSettings)
  .settings(scalacOptions in Tut := (scalacOptions in Tut).value.filterNot(Set("-Ywarn-unused-import", "-Xfatal-warnings", "-Xlint")))
  .enablePlugins(GhpagesPlugin)
  .enablePlugins(MicrositesPlugin)
  .enablePlugins(ScalaUnidocPlugin)
  .dependsOn(astJVM, ioJVM, opticsJVM)

lazy val root = (project in file("."))
  .commonSettings
  .settings(
    name := "xml-lens"
  )
  .aggregate(astJVM, astJS, ioJVM, ioJS, opticsJVM, opticsJS)


lazy val publishSettings = Seq(
  releaseCrossBuild := true,
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  homepage := Some(url("https://github.com/note/xml-lens")),
  licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT")),
  scmInfo := Some(ScmInfo(url("https://github.com/note/xml-lens"), "scm:git:git@github.com:note/xml-lens.git")),
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("Snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("Releases" at nexus + "service/local/staging/deploy/maven2")
  },
  pomExtra := (
    <developers>
      <developer>
        <id>note</id>
        <name>Michał Sitko</name>
        <url>https://github.com/note/</url>
      </developer>
    </developers>
  )
) ++ credentialSettings

lazy val credentialSettings = Seq(
  credentials ++= (for {
    username <- Option(System.getenv().get("SONATYPE_USERNAME"))
    password <- Option(System.getenv().get("SONATYPE_PASSWORD"))
  } yield Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", username, password)).toSeq
)

lazy val noPublishSettings = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false
)
