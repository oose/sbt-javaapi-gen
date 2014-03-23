name := "sbt-javaapi-gen"

organization := "oose"

version := "0.3"

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % "7.0.6",
  "org.specs2" %% "specs2" % "2.3.10" % "test"
)

sbtPlugin := true
