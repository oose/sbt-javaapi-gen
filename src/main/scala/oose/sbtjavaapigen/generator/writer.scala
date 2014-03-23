package oose.sbtjavaapigen.generator

import scalaz._
import Scalaz._

import Extractors._
import Helper._
import JavaMethod._

import oose.sbtjavaapigen.generator.Helper._

object Writer {
  
  implicit class MethodWriter(method: JavaMethod) {
    def write(tabulator: Int = 0): ImportWriter[Option[String]] = {
      val tabStr = tab(tabulator)

      // only generate for non static, public methods
      method match {
        case PublicNonStaticMethod(name, params, 0, returnType) =>
          method match {
            case Getter(_, scalaName) =>
              s"${tabStr}def ${scalaName} = java.${name}()".some.set(NoImport)
            case BooleanGetter(_, scalaName) =>
              s"${tabStr}def ${scalaName} = java.${name}()".some.set(NoImport)
            case _ => None.set(List.empty)
          }
        case PublicNonStaticMethod(name, params, 1, returnType) =>
          method match {
            case Setter(_, scalaName) =>
              val p = params.head
              s"${tabStr}def ${scalaName}(arg0 : ${p.name}) = java.${name}(arg0)".some.set(List(p.typePackage))
            case _ => None.set(NoImport)
          }
        case _ => None.set(NoImport)
      }
    }
  }
  /**
   * Create an implicit class with scala methods if the
   * original javalcass contains methods.
   * @param tabulator indent by some tabs
   */
  implicit class ClassWriter(clazz: JavaClass) {
    def write(tabulator: Int = 0): ImportWriter[Option[String]] = {
      val tabStr = tab(tabulator)
      val clazzPackage = clazz.classPackage
      val methodWriter = ((clazz.methods.map(m => m.write(tabulator + 1)).sequenceU)) :++> List(clazzPackage)
      val methodStrings = methodWriter.value.flatten.mkString("\n")
 
      (if (!methodStrings.isEmpty) {
        s"""|${tabStr}implicit class Scala${clazz.name}(val java : ${clazz.name}) {
    	  |${methodStrings}
    	  |$tabStr}""".stripMargin.some
      } else {
        none[String]
      }).set(methodWriter.written)
    }
  }

  implicit class PackageWriter(result: Map[ClassPackage, List[JavaClass]]) {
    private def writePackage(content: Set[JavaClass])(tabulator: Int = 1): ImportWriter[String] = {
      val tabStr = tab(tabulator)

      val writer = content.map(c => c.write(tabulator + 1)).toList.sequenceU
      val stringRepr = writer.value.flatten.mkString("\n")
      stringRepr.set(writer.written)
    }

    def write() = {
      result.map {
        case (pack, content) =>
          val writtenPackage = writePackage(content.toSet)(tabulator = 1)
          val importsNames = (for { 
            p <- writtenPackage.written
            name <- p.realName 
          } yield name).toSet

          pack.scalaName match {
            case Some(packageName) =>
              s"""|package ${packageName} {
        	    |
        	    |  object api {
        	    |${importsNames.map(x => s"    import $x._").mkString("\n")}
        	    |${writtenPackage.value}
        	    |  }
        	    |}
        	    |""".stripMargin
            case None => ""
          }
      }
    }.mkString("\n")
  }

}