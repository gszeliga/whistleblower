import sbt.Keys._

name := "whistleblower"

version := "1.0"

scalaVersion := "2.11.6"

libraryDependencies ++= {
  Seq(
    "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test" withSources(),
    "org.scala-lang.modules" % "scala-async_2.11" % "0.9.4" withSources(),
    "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
    "org.slf4j" % "slf4j-api" % "1.7.12",
    "ch.qos.logback" % "logback-classic" % "1.1.3",
    "junit" % "junit" % "4.11" % "test" withSources()
  )
}

resolvers += Resolver.mavenLocal



