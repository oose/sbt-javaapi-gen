package oose.sbtjavaapigen

import sbt._
import sbt.IO
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
  lazy val javaapi =
    taskKey[Seq[File]]("Generates a set of implicit classes to access java classes")

  /**
   * Settings for the set of classes to be transformed to implicit classes.
   */
  lazy val javaapiclasses = settingKey[Set[String]]("Classes to generate")

  lazy val javaapiTask =
    (externalDependencyClasspath in Compile,
      streams,
      (sourceManaged in Compile),
      javaapiclasses) map {
        (fc, s, output, jac) =>
          val classPath = fc.files
          invokeGenerator(fc.files, s.log, output / "scala" / "api.scala", jac)
      }

  /**
   * Provide default settings.
   */
  override val settings = Seq(
    sourceGenerators in Compile <+= (javaapi in Compile),
    javaapiclasses := Set.empty,
    javaapi <<= javaapiTask)
}