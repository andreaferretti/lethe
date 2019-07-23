// import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

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

// lazy val macros = project.in(file("macros"))
//   .settings(commonSettings: _*)
//   .settings(
//     libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value
//   )

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
  .aggregate(letheServer)

// lazy val messagesJVM = messages.jvm
// lazy val messagesJS = messages.js
// lazy val letheJVM = lethe.jvm
// lazy val letheJS = lethe.js

lazy val letheServer = project.in(file("server"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      zeromq,
      "org.iq80.leveldb" % "leveldb" % "0.7"
    )
  )
  .dependsOn(messages)