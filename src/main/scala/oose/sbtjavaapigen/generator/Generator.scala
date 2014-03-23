package oose.sbtjavaapigen.generator

import java.io.File
import java.net.URLClassLoader

import scala.util._

import scalaz._
import Scalaz._

import Writer._
import Extractors._

/**
 * Entry point for the generator.
 */
object Generator {

  type ErrorWriter[A] = Writer[List[String], A]

  private def obtainClazz(className: String, classLoader: ClassLoader): ErrorWriter[Option[JavaClass]] = {
    Try {
      JavaClass(classLoader.loadClass(className))
    }.toOption match {
      case result @ Some(jc) =>
        if (jc.hasTypeParameters)
          None.set(List(s"$className has type parameters. Can't deal with that yet."))
        else
          result.set(List.empty)
      case fail @ None =>
        fail.set(List(s"$className not found"))
    }
  }

  /**
   * @param classpath A list of jars to load into the classloader
   * @param classes A set of fully qualified class names
   */
  def apply(classpath: Seq[File], classes: Set[String]) = {
    val parentClassloader = this.getClass().getClassLoader()
    val urls = classpath.map { _.toURI.toURL }.toArray
    val urlClassLoader = new URLClassLoader(urls, parentClassloader)
    val writerResult = (classes.map { name => obtainClazz(name, urlClassLoader) }).toList.sequenceU
    val output = writerResult.value.flatten.groupBy { _.classPackage }.write
    (writerResult.written, output)
  }
}

