name := "lethe"

val zeromq = "org.zeromq" % "jeromq" % "0.3.5"

val commonSettings = Seq(
  scalaVersion := "2.11.7",
  organization := "unicredit",
  version := "0.1.1-SNAPSHOT",
  scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
    "-language:postfixOps"
  )
)

lazy val macros = crossProject.in(file("macros"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies <+= (scalaVersion)
      ("org.scala-lang" % "scala-reflect" % _)
  )

lazy val messages = crossProject.in(file("messages"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "me.chrons" %%% "boopickle" % "1.1.0"
    )
  )

lazy val lethe = crossProject.in(file("."))
  .settings(commonSettings: _*)
  .jvmSettings(
    libraryDependencies ++= Seq(
      zeromq,
      "com.github.pathikrit" %% "better-files" % "2.13.0",
      "com.github.tototoshi" %% "scala-csv" % "1.2.2",
      "org.monifu" %%% "minitest" % "0.14" % "test"
    ),
    testFrameworks += new TestFramework("minitest.runner.Framework")
  )
  .jsSettings(
    persistLauncher in Compile := true,
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.8.0",
      "be.doeraene" %%% "scalajs-jquery" % "0.8.1"
    )
  )
  .dependsOn(messages)

lazy val messagesJVM = messages.jvm
lazy val messagesJS = messages.js
lazy val letheJVM = lethe.jvm
lazy val letheJS = lethe.js

lazy val letheServer = project.in(file("server"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      zeromq,
      "org.iq80.leveldb" % "leveldb" % "0.7"
    )
  )
  .dependsOn(messages.jvm)

lazy val root = project.in(file(".")).aggregate(letheJVM, letheJS, letheServer)