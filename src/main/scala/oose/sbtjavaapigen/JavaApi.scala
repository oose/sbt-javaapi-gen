package oose.sbtjavaapigen

import sbt._
import sbt.IO
import sbt.Def.Initialize
import Keys._
import complete.DefaultParsers._
import oose.sbtjavaapigen.generator._

/**
 * Plugin entry point for the
 * sbt-javaapi-gen plugin
 * see: https://github.com/oose/sbt-javaapi-gen
 */
object JavaApi extends Plugin {

  /**
   * Setting for the generator task.
   */
  lazy val javaapi =
    taskKey[Seq[File]]("Generates a set of implicit classes to access java classes")

  /**
   * Settings for the set of classes to be transformed to implicit classes.
   */
  lazy val javaapiclasses = settingKey[Set[String]]("Classes to generate")

  /**
   * A list of warnings and the result of the generation process.
   */
  type Response = (List[String], String)

 

  // TODO output file is hardcoded - fail
  /**
   * Task definition.
   */
    lazy val javaapiTaskImpl: Initialize[Task[Seq[File]]] =
      Def.task {
        val output = ((sourceManaged in Compile) / "/scala/api.scala").value
        val log = streams.value.log
        log.info("Generating sources...")
        val result: Response = Generator(javaapiclasses.value)
        IO.write(output, result._2)
        result._1.map(classNotFound => log.warn(classNotFound))
        Seq(output)
      } 

  /**
   * Provide default settings.
   */
  override val settings = Seq(
    sourceGenerators in Compile <+= (javaapi in Compile),
    javaapiclasses := Set.empty,
    javaapi <<= javaapiTaskImpl)
}