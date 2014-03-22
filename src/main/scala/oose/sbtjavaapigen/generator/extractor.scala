package oose.sbtjavaapigen.generator
import scalaz._
import syntax.std.option._
import Helper._

object Extractors {

  object JavaClassEx {
    def unapply(jc: JavaClass) = (jc.name, jc.classPackage, jc.methods, jc.methodCount).some
  }

  object MethodEx {
    def unapply(jm: JavaMethod) = (jm.name, jm.params, jm.paramCount, jm.returnType).some
  }

  object Getter {
    def unapply(jm: JavaMethod) = if (jm.name.startsWith("get")) (jm, jm.name.substring(3).scalaConvention).some else None
  }

  object BooleanGetter {
    def unapply(jm: JavaMethod) = if (jm.name.startsWith("is")) (jm, jm.name).some else None
  }

  object Setter {
    def unapply(jm: JavaMethod) = if (jm.name.startsWith("set")) (jm, jm.name.substring(3).scalaConvention + "_=").some else None
  }

}