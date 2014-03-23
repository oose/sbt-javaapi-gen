package oose.sbtjavaapigen.generator

import java.lang.reflect._
import Helper._

/**
 * Represents a java Class[_].
 */
case class JavaClass(clazz: Class[_]) {
  lazy val name = clazz.getSimpleName()
  lazy val fullName = clazz.getName()
  lazy val classPackage = ClassPackage(clazz.getPackage())
  lazy val methods = clazz.getMethods().map(m => JavaMethod(m)).toList
  lazy val methodCount = methods.length
}

/** Represents a package. */
case class ClassPackage(p: Package) {
  lazy val name = Option(p).map(p => p.getName())
}

/** Represents some Type. */
case class JavaType(t: Type) {
  lazy val (name: String, typePackage: ClassPackage) = t match {
    case c: Class[_] =>
      val name = if (c.isPrimitive())
        c.getSimpleName() match {
          case "byte" => "Byte"
          case "short" => "Short"
          case "int" => "Int"
          case "long" => "Long"
          case "float" => "Float"
          case "double" => "Double"
          case "boolean" => "Boolean"
          case "char" => "Char"

          case _ => c.getSimpleName()
        }
      else c.getSimpleName()

      (name, ClassPackage(c.getPackage()))
    case p: ParameterizedType =>
      val rawType = JavaType(p.getRawType())
      val actualTypes = p.getActualTypeArguments().map(t => JavaType(t))
      val name = s"${rawType.name}[${actualTypes.map(_.name).mkString(",")}]"
      (name, ClassPackage(p.getClass().getPackage()))
    case w: WildcardType =>
      // TODO this can't work properly
      ("AnyRef", ClassPackage(w.getClass().getPackage()))
  }

}

/**
 * Reprensents a Java method.
 */
case class JavaMethod(method: Method) {
  lazy val name = method.getName()
  lazy val isStatic = Modifier.isStatic(method.getModifiers())
  lazy val isPublic = Modifier.isPublic(method.getModifiers())
  lazy val params = method.getGenericParameterTypes().map(t => JavaType(t))
  lazy val paramCount = params.length
  lazy val returnType = JavaType(method.getGenericReturnType())
}

object JavaMethod {
  val NoImport = List.empty[ClassPackage]
}
