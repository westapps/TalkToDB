import sbt.Keys._
import sbt._

lazy val sprintBootVersion = "3.2.8"
lazy val sprintCloudVersion = "4.1.4"
lazy val circeVersion = "0.14.5"
lazy val r2dbcVersion = "1.0.0.RELEASE"
lazy val awsSDKVersion = "2.25.1"

resolvers ++= Seq(
  "Maven Central" at "https://repo1.maven.org/maven2/",
  "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases/",
  Resolver.mavenLocal
)

fork := true

enablePlugins(JavaAppPackaging)

javacOptions ++= Seq("-source", "21", "-target", "21")

lazy val root = (project in file("."))
  .settings(
    inThisBuild(List(
      organization := "com.westapps",
      scalaVersion := "2.13.12"
    )),
    name := "ai-talktodb",
    version := "1.0",
    libraryDependencies ++= Seq(
      "com.squareup.okhttp3" % "okhttp" % "4.10.0",
      "com.squareup.okhttp3" % "logging-interceptor" % "4.11.0",

      "org.slf4j" % "slf4j-api" % "1.7.36",
      "ch.qos.logback" % "logback-classic" % "1.2.11",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5" exclude("org.slf4j", "slf4j-api"),
      "net.logstash.logback" % "logstash-logback-encoder" % "7.3",

      "org.springframework.boot" % "spring-boot-starter-webflux" % sprintBootVersion,
      "org.springframework.boot" % "spring-boot-starter-actuator" % sprintBootVersion,
      "org.springframework.boot" % "spring-boot-starter-validation" % sprintBootVersion,
      // "org.springframework.boot" % "spring-boot-starter-data-redis" % sprintBootVersion,
      "org.springframework.boot" % "spring-boot-starter-security" % sprintBootVersion,
      // "org.springframework.boot" % "spring-boot-starter-data-r2dbc" % sprintBootVersion,
      // "org.springframework.cloud" % "spring-cloud-starter-gateway" % sprintCloudVersion,
      // "org.springframework.cloud" % "spring-cloud-starter-circuitbreaker-reactor-resilience4j" % "3.1.1",
      // "org.springframework.boot" % "spring-boot-starter-data-jpa" % sprintBootVersion,
      // "org.springframework.boot" % "spring-boot-starter-data-redis-reactive" % sprintBootVersion,
      // "org.springframework.cloud" % "spring-cloud-gateway-webflux" % sprintCloudVersion,

      // "io.awspring.cloud" % "spring-cloud-aws-starter-sqs" % "3.1.0" exclude("commons-logging", "commons-logging"),

      "jakarta.servlet" % "jakarta.servlet-api" % "6.1.0" % "provided",
      "com.sun.mail" % "jakarta.mail" % "2.0.1" exclude("commons-logging", "commons-logging"),

      "io.r2dbc" % "r2dbc-pool" % r2dbcVersion,
      "org.mariadb" % "r2dbc-mariadb" % "1.1.4",
      "org.liquibase" % "liquibase-core" % "4.20.0" % Runtime,
      // "org.springframework.boot" % "spring-boot-starter-data-jdbc" % sprintBootVersion % Runtime exclude("org.slf4j", "slf4j-api"),
      "mysql" % "mysql-connector-java" % "8.0.33" % Runtime,

      "com.kjetland" %% "mbknor-jackson-jsonschema" % "1.0.39",
      "com.networknt" % "json-schema-validator" % "1.0.82",
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.15.3",
      "com.beachape" %% "enumeratum" % "1.7.2",
      "com.github.pjfanning" %% "jackson-module-enumeratum" % "2.14.1",

      "io.micrometer" % "micrometer-registry-prometheus" % "1.10.5",
      "org.springdoc" % "springdoc-openapi-starter-webflux-ui" % "2.3.0",
      "software.amazon.awssdk" % "sts" % awsSDKVersion exclude("commons-logging", "commons-logging"),
      "software.amazon.awssdk" % "ses" % awsSDKVersion exclude("commons-logging", "commons-logging"),
      "software.amazon.awssdk" % "sqs" % awsSDKVersion exclude("commons-logging", "commons-logging"),
      "software.amazon.awssdk" % "dynamodb" % awsSDKVersion exclude("commons-logging", "commons-logging"),
      "software.amazon.awssdk" % "lambda" % awsSDKVersion exclude("commons-logging", "commons-logging"),
      "software.amazon.awssdk" % "s3" % awsSDKVersion exclude("commons-logging", "commons-logging"),
      "software.amazon.awssdk" % "sns" % awsSDKVersion exclude("commons-logging", "commons-logging"),

      // "software.amazon.awssdk" % "bedrockruntime" % awsSDKVersion,
      "com.github.ben-manes.caffeine" % "caffeine" % "3.1.6",
      //"javax.servlet" % "javax.servlet-api" % "4.0.1",
      "commons-io" % "commons-io" % "2.15.1",
      "io.lettuce" % "lettuce-core" % "6.3.2.RELEASE",
      // "io.github.sashirestela" % "simple-openai" % "3.3.0" exclude("com.fasterxml.jackson.core", "jackson-databind"),

      "io.projectreactor" % "reactor-test" % "3.5.4" % Test,
      "org.springframework.boot" % "spring-boot-starter-test" % sprintBootVersion % Test,
      "org.springframework.security" % "spring-security-test" % "6.0.2" % Test,
      "net.aichler" % "jupiter-interface" % JupiterKeys.jupiterVersion.value % Test,
      "com.h2database" % "h2" % "2.1.214" % Test,
      "it.ozimov" % "embedded-redis" % "0.7.3" % Test exclude("commons-logging", "commons-logging"),
      "io.r2dbc" % "r2dbc-h2" % r2dbcVersion % Test,
      "org.assertj" % "assertj-core" % "3.24.2" % Test,
      "com.squareup.okhttp3" % "mockwebserver" % "4.10.0" % Test,
      "net.datafaker" % "datafaker" % "2.2.2" % Test,
      "io.vavr" % "vavr" % "0.10.4" % Test,
      "org.junit.jupiter" % "junit-jupiter-api" % "5.8.1" % Test,
      "org.testcontainers" % "testcontainers" % "1.20.4" % Test,
      "org.testcontainers" % "junit-jupiter" % "1.20.4" % Test,

      "io.netty" % "netty-resolver-dns-native-macos" % "4.1.84.Final" % Runtime classifier "osx-aarch_64"
    ),
    ThisBuild / scapegoatVersion := "2.1.3",
    scalacOptions += "-deprecation"
  )

sonarProperties := Map(
  "sonar.java.binaries" -> "target/scala-2.13/classes"
)
