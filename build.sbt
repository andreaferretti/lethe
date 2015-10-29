name := "ORAM"

lazy val oram = crossProject.in(file("."))
  .settings(
    scalaVersion := "2.11.7",
    organization := "unicredit",
    version := "0.1.0",
    scalacOptions ++= Seq(
        "-deprecation",
        "-feature"
    )
  )
  .jvmSettings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" % "akka-actor_2.11" % "2.4.0"
    )
  )
  .jsSettings(
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.8.0"
    )
  )


lazy val oramJVM = oram.jvm
lazy val oramJS = oram.js

lazy val root = project.in(file(".")).aggregate(oramJVM, oramJS)