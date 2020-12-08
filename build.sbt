name := "akka-essentials"

version := "0.1"

scalaVersion := "2.12.7"

val akkaVersion = "2.5.13"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion
)