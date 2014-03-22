package oose.sbtjavaapigen

import sbt._
import sbt.IO
import sbt.Def.Initialize
import Keys._
import complete.DefaultParsers._
import oose.sbtjavaapigen.generator._

object JavaApi extends Plugin {

  lazy val javaapi = taskKey[Seq[File]]("Generates a scala API for java classes")

  lazy val javaapiclasses = settingKey[Set[String]]("Classes to generate")
  
  // TODO output file is hardcoded - fail
  lazy val javaapiTaskImpl: Initialize[Task[Seq[File]]] =
   Def.task { 
      val output = ((sourceManaged in Compile)/ "/scala/api.scala").value
      val log = streams.value.log
      log.info("Generating sources...")
      IO.write(output,Generator(javaapiclasses.value))
      Seq(output)   
  }
  
  override val settings = Seq( 
      sourceGenerators in Compile <+= (javaapi in Compile), 
      javaapiclasses := Set.empty,
      javaapi := javaapiTaskImpl.value )
}