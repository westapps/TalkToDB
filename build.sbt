import sbtassembly.AssemblyPlugin
import sbtassembly.AssemblyPlugin.autoImport._
import sbtassembly.MergeStrategy

ThisBuild / scalaVersion := "2.13.12"

val springBootVersion = "3.1.3"

val testLibs = Seq(
  "org.junit.jupiter" % "junit-jupiter-engine" % "5.10.0" % Test,
  "io.projectreactor" % "reactor-test" % "3.5.12" % Test,
  "org.mockito" % "mockito-core" % "5.5.0" % Test,
  "org.junit.jupiter" % "junit-jupiter-api" % "5.10.0" % Test,
  "org.springframework.boot" % "spring-boot-starter-test" % springBootVersion % Test,
  "org.assertj" % "assertj-core" % "3.24.2" % Test
)

lazy val root = (project in file("."))
  .enablePlugins(AssemblyPlugin)
  .settings(
    name := "TalkToDB",
    version := "0.1.0",
    Compile / mainClass := Some("com.westapps.talktodb.Application"),
    libraryDependencies ++= Seq(
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5" exclude ("org.slf4j", "slf4j-api"),
      //
      "org.springframework.boot" % "spring-boot-starter-webflux" % springBootVersion,
      "org.springframework.boot" % "spring-boot-starter" % springBootVersion,
      "org.springframework.boot" % "spring-boot-starter-security" % springBootVersion,
      "org.springframework.boot" % "spring-boot-starter-web" % springBootVersion,
      "org.springframework.boot" % "spring-boot-starter-actuator" % springBootVersion,
      "org.springframework.boot" % "spring-boot-starter-mail" % springBootVersion,
      "org.springframework.boot" % "spring-boot-starter-json" % springBootVersion,
      "org.springframework.boot" % "spring-boot-starter-validation" % springBootVersion,
      "org.springframework" % "spring-context" % "6.0.11",
      //
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.15.2",
      "org.scala-lang.modules" %% "scala-java8-compat" % "1.0.2",
      "com.lihaoyi" %% "scalatags" % "0.13.1",
      "com.github.pjfanning" %% "jackson-module-enumeratum" % "2.14.1",
      "commons-io" % "commons-io" % "2.18.0",
      //aws
      "software.amazon.awssdk" % "ses" % "2.29.15"
    ) ++ testLibs,
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", "spring.handlers")  => MergeStrategy.concat
      case PathList("META-INF", "spring.schemas")   => MergeStrategy.concat
      case PathList("META-INF", "spring.tooling")   => MergeStrategy.last
      case PathList("META-INF", "spring.factories") => MergeStrategy.concat
      case PathList("META-INF", "MANIFEST.MF")      => MergeStrategy.discard
      case PathList("META-INF", xs @ _*)            => MergeStrategy.discard
      case PathList("module-info.class")            => MergeStrategy.discard
      case x =>
        val oldStrategy = (assembly / assemblyMergeStrategy).value
        oldStrategy(x)
    }
  )
