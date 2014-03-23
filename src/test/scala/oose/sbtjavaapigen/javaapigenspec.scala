package oose.sbtjavaapigen

import oose.sbtjavaapigen.generator._
import org.specs2.mutable._

class JavaApiGenSpec extends SpecificationWithJUnit {

  def wrapInPackage(packageName: String)(c: String) = (packageName + "." + c)

  "The API Generator for java.io.File" should {
    val io = Set(
      "File") map wrapInPackage("java.io")

    val (warnings, output) = Generator(Seq.empty,
      io)
    "run without warnings" in {
      warnings must beEmpty
    }

    "create output" in {
      output must not beEmpty
    }

    "create the correct package" in {
      output must contain("package genjava.io")
    }
    
    "import java.io" in {
      output must contain("import java.io")
    }

    "contain an api object" in {
      output must contain("object api")
    }

    "create the correct class" in {
      output must contain("implicit class ScalaFile")
    }

    "treats getClass correctly" in {
      output must contain("def Class =")
    }
  }

  "The API Generator for java.lang.Package" should {
    val io = Set(
      "Package") map wrapInPackage("java.lang")
    val (warnings, output) = Generator(Seq.empty,
      io)

    "treat static methods correctly" in {
      output must not contain ("def packages")
    }
  }

  "The API Generator for java.lang.Thread" should {
    val io = Set(
      "Thread") map wrapInPackage("java.lang")
    val (warnings, output) = Generator(Seq.empty,
      io)

    "import java.lang" in {
      output must contain("import java.lang")
    }

    "create package genjava.lang" in {
      output must contain("package genjava.lang")
    }
  }

  "The API Generator for java.lang.Class" should {
    val io = Set(
      "Class") map wrapInPackage("java.lang")
    val (warnings, output) = Generator(Seq.empty,
      io)

    "ignore that class because it has type parameters" in {
      //warnings must not beEmpty
      warnings(0) must contain("Class has type parameters.")
    }
  }

}