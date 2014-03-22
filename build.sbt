name := "sbt-javaapi-gen"

organization := "oose"

version := "0.1"

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % "7.0.6"
)

sbtPlugin := true

val snapshots = Some(Resolver.file("Local github (snapshots)",  
  new File(Path.userHome.absolutePath+"/oose.github.io/m2/snapshots")))

val releases =  Some(Resolver.file("Local github (releases)", 
  new File(Path.userHome.absolutePath+"/oose.github.io/m2/releases")))

publishMavenStyle := true

publishTo <<= version { (v: String) =>
  if (v.trim.endsWith("SNAPSHOT"))
    snapshots
  else
    releases
}
