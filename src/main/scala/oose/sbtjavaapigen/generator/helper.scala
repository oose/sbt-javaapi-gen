package oose.sbtjavaapigen.generator

import scalaz._

object Helper {
  
  /**
   *  Define a Writer to store required Imports
   */
  type ImportWriter[A] = Writer[List[ClassPackage], A]

  /**
   *  Prepend some tabulators as spaces in front of a string.
   */
  def tab(tabulator: Int): String = List.fill(tabulator * 2)(" ").mkString

  implicit class MethodString(m: String) {

    /**
     * converts a property name in CamelCase which has been
     * derived from a java function, e.g. getMyFoo() [MyFoo] to
     * myFoo, taking into account that scala has some extra
     * reserved keywords.
     * For example getType() is not turned into the lower
     * case version.
     */
    def scalaConvention() =
      if (m == null) null
      else if (m.length == 0) ""
      else {
        m match {
          case "Class" => "Class"
          case "Type" => "Type"
          case "Sealed" => "Sealed"
          case "Yield" => "Yield"
          case "Match" => "Match"
          case "Implicit" => "Implicit"
          case "Val" => "Val"
          case "Var" => "Var"
          case _ =>
            val chars = m.toCharArray
            chars(0) = chars(0).toLower
            new String(chars)
        }
      }
  }
}