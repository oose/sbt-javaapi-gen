package oose.sbtjavaapigen.generator

object Writer {
  import Extractors._
  import JavaMethod._
  import Helper._
  import scalaz._
  import Scalaz._

  implicit class MethodWriter(method: JavaMethod) {
    def write(tabulator: Int = 0): ImportWriter[Option[String]] = {
      val tabStr = tab(tabulator)

      method match {
        case MethodEx(name, params, 0, returnType) =>
          method match {
            case Getter(_, scalaName) =>
              s"${tabStr}def ${scalaName} = java.${name}()".some.set(NoImport)
            case BooleanGetter(_, scalaName) =>
              s"${tabStr}def ${scalaName} = java.${name}()".some.set(NoImport)
            case _ => None.set(List.empty)
          }
        case MethodEx(name, params, 1, returnType) =>
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
      val methodWriter = clazz.methods.map(m => m.write(tabulator + 1)).sequenceU
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

  implicit class PackageWriter(result: Map[ClassPackage, Set[JavaClass]]) {
    private def writePackage(content: Set[JavaClass])(tabulator: Int = 1): ImportWriter[String] = {
      val tabStr = tab(tabulator)

      val writer = content.map(c => c.write(tabulator + 1)).toList.sequenceU
      val stringRepr = writer.value.flatten.mkString("\n")
      stringRepr.set(writer.written)
    }

    def write() = {
      result.map {
        case (pack, content) =>
          val writtenPackage = writePackage(content)(tabulator = 1)
          val importsNames = (for {
            p <- writtenPackage.written
            name <- p.name if !(name.startsWith("java.lang") || name.startsWith("sun."))
          } yield name).toSet

          pack.name match {
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