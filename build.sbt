name := "lethe"

val zeromq = "org.zeromq" % "jeromq" % "0.3.5"

val commonSettings = Seq(
  scalaVersion := "2.12.8",
  organization := "unicredit",
  version := "0.1.1-SNAPSHOT",
  scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
    "-language:postfixOps"
  )
)

lazy val messages = project.in(file("messages"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "io.suzaku" %% "boopickle" % "1.3.0"
    )
  )

lazy val lethe = project.in(file("."))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      zeromq,
      "com.github.pathikrit" %% "better-files" % "3.6.0",
      "com.github.tototoshi" %% "scala-csv" % "1.3.5",
      "io.monix" %% "minitest" % "2.2.1" % "test"
    ),
    testFrameworks += new TestFramework("minitest.runner.Framework")
  )
  .dependsOn(messages)

lazy val server = project.in(file("server"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      zeromq,
      "org.iq80.leveldb" % "leveldb" % "0.7"
    )
  )
  .dependsOn(messages)

lazy val apps = project.in(file("apps"))
  .settings(commonSettings: _*)
  .dependsOn(lethe)