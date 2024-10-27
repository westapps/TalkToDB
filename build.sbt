name := "TalkToDB"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.13.12"

libraryDependencies ++= Seq(
  "org.springframework.boot" % "spring-boot-starter" % "3.1.0",
  "org.springframework.boot" % "spring-boot-starter-web" % "3.1.0",
  "org.springframework.boot" % "spring-boot-starter-actuator" % "3.1.0",
  "org.springframework.boot" % "spring-boot-starter-test" % "3.1.0" % Test,
  "org.scala-lang.modules" %% "scala-java8-compat" % "1.0.2"
)

enablePlugins(SpringBootPlugin)

resolvers += "Spring Milestone" at "https://repo.spring.io/milestone"

mainClass in Compile := Some("com.westapps.talktodb.TalkToDBApplication")

// Optional: If you want to package your application as a JAR
assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}



