package oose.sbtjavaapigen.generator

import java.io.File
import java.net.URLClassLoader

import scala.util._

import scalaz._
import Scalaz._

import Writer._
import Extractors._

object Generator {

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

