package oose.sbtjavaapigen.generator

import java.lang.reflect._
import scalaz._
import Scalaz._
import Helper._

object Generator {

  import Extractors._
  import Writer._
  import scala.util._
  
  type ErrorWriter[A] = Writer[List[String], A]

  private def obtainClazz(className: String) : ErrorWriter[Option[JavaClass]] = {
    Try {
      JavaClass(Class.forName(className))
    } toOption match {
      case result @ Some(_) => result.set(List.empty)
      case fail @ None =>
        fail.set(List(s"$className not found"))
    }
  }

  def apply(classes: Set[String]) = {
     val writerResult = (classes map obtainClazz).toList.sequenceU
     val output = writerResult.value.flatten.groupBy{ _.classPackage }.write
     (writerResult.written, output)
  }
}

