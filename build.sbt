import sbtassembly.AssemblyPlugin
import sbtassembly.AssemblyPlugin.autoImport._
import sbtassembly.MergeStrategy

ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .enablePlugins(AssemblyPlugin)
  .settings(
    name := "TalkToDB",
    version := "0.1.0",
    Compile / mainClass := Some("com.westapps.talktodb.main.Application"),

    libraryDependencies ++= Seq(
      "org.springframework.boot" % "spring-boot-starter" % "3.1.3",
      "org.springframework.boot" % "spring-boot-starter-web" % "3.1.3",
      "org.springframework.boot" % "spring-boot-starter-actuator" % "3.1.3",
      "org.springframework.boot" % "spring-boot-starter-test" % "3.1.3" % Test,
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.15.2",
      "org.scala-lang.modules" %% "scala-java8-compat" % "1.0.2",
      "com.github.pjfanning" %% "jackson-module-enumeratum" % "2.14.1"
    ),

    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case PathList("META-INF", "io.netty.versions.properties") => MergeStrategy.first
      case x => MergeStrategy.first
    }
  )






