package oose.sbtjavaapigen.generator

import java.lang.reflect._
import scalaz._
import Scalaz._
import Helper._
import java.net.URLClassLoader
import java.io.File

object Generator {

  import Extractors._
  import Writer._
  import scala.util._

  type ErrorWriter[A] = Writer[List[String], A]

  private def obtainClazz(className: String, classLoader : ClassLoader): ErrorWriter[Option[JavaClass]] = {
    Try {
      JavaClass(classLoader.loadClass(className))
    } toOption match {
      case result @ Some(_) => result.set(List.empty)
      case fail @ None =>
        fail.set(List(s"$className not found"))
    }
  }

  def apply(classpath: Seq[File], classes: Set[String]) = {
    val parentClassloader = this.getClass().getClassLoader()
    val urls = classpath.map { _.toURL() }.toArray
    val urlClassLoader = new URLClassLoader(urls, parentClassloader)
    val writerResult = (classes.map{ name => obtainClazz(name, urlClassLoader)}).toList.sequenceU
    val output = writerResult.value.flatten.groupBy { _.classPackage }.write
    (writerResult.written, output)
  }
}

