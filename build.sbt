name := "iroha-scala"

version := "1.0.0-SNAPSHOT"

val PROJECT_SCALA_VERSION = "2.11.8"

scalaVersion := PROJECT_SCALA_VERSION

useGpg in GlobalScope := true

lazy val libraries = Seq(
  "org.scala-lang" % "scala-library" % "2.11.8",
  "org.scala-lang" % "scala-reflect" % "2.11.8",
  "org.scala-lang.modules" %% "scala-xml" % "1.0.4",
  "io.grpc" % "grpc-core" % "1.2.0",
  "io.grpc" % "grpc-netty" % "1.2.0",
  "io.grpc" % "grpc-stub" % "1.2.0",
  "commons-io" % "commons-io" % "2.5",
  "org.bouncycastle" % "bcpg-jdk15on" % "1.51",
  "net.i2p.crypto" % "eddsa" % "0.1.0",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test"
)

lazy val settings = Seq(
  organization := "org.hyperledger",
  scalaVersion := PROJECT_SCALA_VERSION,
  javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-encoding", "UTF-8"),
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

lazy val flatbuffers = (project in file("flatbuffers"))
  .settings(name := "flatbuffers")
  .settings(settings: _*)
  .settings(publish := {})

lazy val schema = (project in file("schema")).dependsOn(flatbuffers)
  .settings(name := "schema")
  .settings(settings: _*)
  //.settings(compileJavaSchemaTask)
  //.settings(compile <<= compile in Compile dependsOn compileJavaSchema)
  .settings(javaSource in Compile := baseDirectory.value / "src" / "main" / "gen-java")

lazy val irohaScala = (project in file("iroha-scala")).dependsOn(schema, flatbuffers)
  .settings(settings: _*)
  .settings(name := "iroha-scala")

lazy val compileJavaSchema = taskKey[Unit]("Run flatc compiler to generate Java classes for schema")
lazy val compileJavaSchemaTask = compileJavaSchema := {
  val schemaFiles = "ls -1 schema/flatbuffers/".!!.split("\n")
  schemaFiles.filter(_.endsWith(".fbs")).foreach { schema =>
    val result = s"flatc -j -o schema/src/main/gen-java schema/flatbuffers/$schema".!!
    println(s"*** Generated Java classes from FlatBuffer schema $schema.  Results: $result")
  }
}
