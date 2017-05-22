name := "iroha-scala"

version := "1.0.0"

val PROJECT_SCALA_VERSION = "2.11.11"

scalaVersion := PROJECT_SCALA_VERSION

useGpg in GlobalScope := true

lazy val libraries = Seq(
  "com.wordnik.swagger" %% "swagger-async-httpclient" % "0.3.5",
  "joda-time" % "joda-time" % "2.3",
  "org.joda" % "joda-convert" % "1.3.1",
  "ch.qos.logback" % "logback-classic" % "1.0.13" % "provided",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "org.bouncycastle" % "bcpg-jdk15on" % "1.51",
  "net.i2p.crypto" % "eddsa" % "0.1.0",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test"
)

lazy val settings = Seq(
  organization := "org.hyperledger",
  scalaVersion := PROJECT_SCALA_VERSION,
  javacOptions ++= Seq("-source", "1.8", "-target", "1.7", "-encoding", "UTF-8"),
  javaOptions ++= Seq("-Xmx1G"),
  scalacOptions ++= Seq(
    "-target:jvm-1.8",
    "-encoding", "UTF-8",
    "-unchecked",
    "-deprecation",
    "-Xfuture",
    "-Yno-adapted-args",
    "-Ywarn-dead-code",
    "-Ywarn-numeric-widen",
    "-Ywarn-value-discard",
    "-Ywarn-unused"
  ),
  libraryDependencies ++= libraries,

  fork in Test := true,

  publishMavenStyle := true,

  publishArtifact in Test := false,

  pomIncludeRepository := { _ => false },

  publishTo <<= version { (v: String) =>
    val nexus = "https://oss.sonatype.org/"
    if (v.trim.endsWith("SNAPSHOT"))
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },

  pomExtra := <url>https://github.com/hyperledger/iroha-scala</url>
    <licenses>
      <license>
        <name>The Apache License, Version 2.0</name>
        <url>http://www.apache.org/licenses/LICENSE-2.0</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:hyperledger/iroha-scala.git</url>
      <connection>scm:git:git@github.com:hyperledger/iroha-scala.git</connection>
    </scm>
    <developers>
      <developer>
        <id>hyperledger</id>
        <name>iroha-scala</name>
        <url>https://github.com/hyperledger/iroha-scala</url>
      </developer>
    </developers>
)

lazy val irohaScala = (project in file("."))
  .settings(settings: _*)
  .settings(name := "iroha-scala")

