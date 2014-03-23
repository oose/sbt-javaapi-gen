package oose.sbtjavaapigen

import sbt._
import Keys._
import oose.sbtjavaapigen.generator._

/**
 * Plugin entry point for the
 * sbt-javaapi-gen plugin
 * see: https://github.com/oose/sbt-javaapi-gen
 */
object JavaApi extends Plugin {

  /**
   * A list of warnings and the result of the generation process.
   */
  type Response = (List[String], String)

  def invokeGenerator(classPath: Seq[File],
    log: sbt.Logger,
    output: java.io.File,
    javaapiClasses: Set[String]) =
    {
      log.info("Generating sources...")
      val result: Response = Generator(classPath, javaapiClasses)
      IO.write(output, result._2)
      result._1.map(classNotFound => log.warn(classNotFound))
      Seq(output)
    }

  /**
   * Setting for the generator task.
   */
  lazy val javaApi =
    taskKey[Seq[File]]("Generates a set of implicit classes to access java classes.")
    
  lazy val javaApiOutput = 
    settingKey[String]("Sets the location and name of the file.")

  /**
   * Settings for the set of classes to be transformed to implicit classes.
   */
  lazy val javaApiClasses = settingKey[Set[String]]("Classes to generate.")

  lazy val javaApiTask =
    (externalDependencyClasspath in Compile,
      streams,
      (sourceManaged in Compile),
      javaApiOutput,
      javaApiClasses) map {
        (fc, s, output, output2, jac) =>
          val classPath = fc.files
          val outputLocation = output / output2
          val result = invokeGenerator(fc.files, s.log, outputLocation, jac)
          s.log.info(s"Wrote result to ${outputLocation}")
          result
      }

  /**
   * Provide default settings.
   */
  override val settings = Seq(
    sourceGenerators in Compile <+= (javaApi in Compile),
    javaApiClasses := Set.empty,
    javaApiOutput := "scala/api.scala",
    javaApi <<= javaApiTask)
}