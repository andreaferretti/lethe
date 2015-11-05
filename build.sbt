name := "ORAM"

val zeromq = "org.zeromq" % "jeromq" % "0.3.5"

val commonSettings = Seq(
  scalaVersion := "2.11.7",
  organization := "unicredit",
  version := "0.1.0",
  scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
    "-language:postfixOps"
  )
)

lazy val messages = crossProject.in(file("messages"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "me.chrons" %%% "boopickle" % "1.1.0"
    )
  )

lazy val oram = crossProject.in(file("."))
  .settings(commonSettings: _*)
  .jvmSettings(
    libraryDependencies ++= Seq(
      zeromq,
      "com.github.pathikrit" %% "better-files" % "2.13.0"
    )
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
lazy val oramJVM = oram.jvm
lazy val oramJS = oram.js

lazy val oramServer = project.in(file("server"))
  .settings(commonSettings: _*)
  .settings(libraryDependencies += zeromq)
  .dependsOn(messages.jvm)

lazy val root = project.in(file(".")).aggregate(oramJVM, oramJS, oramServer)