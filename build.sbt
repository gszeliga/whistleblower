import sbt.Keys._

name := "whistleblower"

version := "1.0"

scalaVersion := "2.11.6"

libraryDependencies ++= {
  Seq(
    "org.scalatest" % "scalatest_2.11" % "2.2.0" % "test" withSources(),
    "junit" % "junit" % "4.11" % "test" withSources(),
    "org.scala-lang.modules" % "scala-async_2.11" % "0.9.4" withSources()
  )
}

resolvers += Resolver.mavenLocal



